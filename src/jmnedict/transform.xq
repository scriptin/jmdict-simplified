xquery version "3.0";
module namespace transform = "transform";

import module namespace j = "http://www.w3.org/2005/xpath-functions";
import module namespace tags = "tags" at "tags.xq";

(:
  Extract a creation date from a comment with following format: "JMnedict created: YYYY-MM-DD".
:)
declare function transform:extract-date($doc as node()) as node() {
  let $version-comment := string($doc//comment()[contains(., 'JMnedict created')][1])
  return <j:string key="dictDate">
    { normalize-space(substring-after($version-comment, ':')) }
  </j:string>
};

(:
  Extract revision numbers, as they appear in comments before DOCTYPE.
  Strictly speaking, these are version numbers of JMnedict, but they are not mentioned in official documentation.
:)
declare function transform:extract-revisions($doc as node()) as node() {
  <j:array key="dictRevisions">
    { for $rev in $doc//comment()[matches(., 'Rev \d+\.\d+')]
      let $first-line := substring-before(string($rev), '&#10;')
      return <j:string> { normalize-space(substring-after($first-line, 'Rev')) } </j:string> }
  </j:array>
};

(:
  Note: ke_pri elements are ignored because they always seem to be missing.
  Try `grep ke_pri JMnedict.xml` to check.
:)
declare function transform:kanji($word-id as xs:string, $elem as node()) as node() {
  <j:map>
    <j:string key="text">
      { $elem/keb/text() }
    </j:string>
    <j:array key="tags">
      { for $info in $elem/ke_inf
        return <j:string> { tags:convert-entity($word-id, $info/text()) } </j:string> }
    </j:array>
  </j:map>
};

(:
  Note: re_pri elements are ignored because they always seem to be missing.
  Try `grep re_pri JMnedict.xml` to check.
:)
declare function transform:kana($word-id as xs:string, $elem as node()) as node() {
  <j:map>
    <j:string key="text">
      { $elem/reb/text() }
    </j:string>
    <j:array ket="tags">
      { for $info in $elem/re_inf
        return <j:string> { tags:convert-entity($word-id, $info/text()) } </j:string> }
    </j:array>
    <j:array key="appliesToKanji">
      { if (count($elem/re_restr) = 0)
        then <j:string> { "*" } </j:string>
        else for $restr in $elem/re_restr
          return <j:string> { $restr/text() } </j:string> }
    </j:array>
  </j:map>
};

declare function transform:xref-part($xref-part as xs:string) as node() {
  if (number($xref-part))
  then <j:number> { number($xref-part) } </j:number>
  else <j:string> { $xref-part } </j:string>
};

declare function transform:xref($xref as node()) as node() {
  <j:array>
    { for $xref-part in tokenize($xref, "・")
      return transform:xref-part($xref-part) }
  </j:array>
};

declare function transform:inner-translation($trans as node()) as node() {
  <j:map>
    <j:string key="lang"> { $trans/@xml:lang/string() } </j:string>
    <j:string key="text"> { $trans/text() } </j:string>
  </j:map>
};

declare function transform:translation($word-id as xs:string, $elem as node()) as node() {
  <j:map>
    <j:array key="type">
      { for $type in $elem/name_type
        return <j:string> { tags:convert-entity($word-id, $type/text()) } </j:string> }
    </j:array>
    <j:array key="related">
      { for $xref in $elem/xref
        return transform:xref($xref) }
    </j:array>
    <j:array key="translation">
      { for $trans in $elem/trans_det
        return transform:inner-translation($trans) }
    </j:array>
  </j:map>
};

declare function transform:word($word as node()) as node() {
  let $word-id := $word/ent_seq/text()
  return <j:map>
    <j:string key="id">
      { $word-id }
    </j:string>
    <j:array key="kanji">
      { for $e in $word/k_ele
        return transform:kanji($word-id, $e) }
    </j:array>
    <j:array key="kana">
      { for $e in $word/r_ele
        return transform:kana($word-id, $e) }
    </j:array>
    <j:array key="translation">
      { for $e in $word/trans
        return transform:translation($word-id, $e) }
    </j:array>
  </j:map>
};

xquery version "3.0";
module namespace transform = "transform";

import module namespace string = "http://zorba.io/modules/string";
import module namespace tags = "tags" at "tags.xq";

(:
  Extract a creation date from a comment with following format: "JMnedict created: YYYY-MM-DD".
:)
declare function transform:extract-date($doc as node()) as node() {
  let $version-comment := string($doc//comment()[contains(., 'JMnedict created')][1])
  return <pair name="dictDate" type="string">
    { normalize-space(substring-after($version-comment, ':')) }
  </pair>
};

(:
  Extract revision numbers, as they appear in comments before DOCTYPE.
  Strictly speaking, these are version numbers of JMnedict, but they are not mentioned in official documentation.
:)
declare function transform:extract-revisions($doc as node()) as node() {
  <pair name="dictRevisions" type="array">
    { for $rev in $doc//comment()[matches(., 'Rev \d+\.\d+')]
      let $first-line := substring-before(string($rev), '&#10;')
      return <item type="string"> { normalize-space(substring-after($first-line, 'Rev')) } </item> }
  </pair>
};

(:
  Note: ke_pri elements are ignored because they always seem to be missing.
  Try `grep ke_pri JMnedict.xml` to check.
:)
declare function transform:kanji($word-id as xs:string, $elem as node()) as node() {
  <item type="object">
    <pair name="text" type="string">
      { $elem/keb/text() }
    </pair>
    <pair name="tags" type="array">
      { for $info in $elem/ke_inf
        return <item type="string"> { tags:convert-entity($word-id, $info/text()) } </item> }
    </pair>
  </item>
};

(:
  Note: re_pri elements are ignored because they always seem to be missing.
  Try `grep re_pri JMnedict.xml` to check.
:)
declare function transform:kana($word-id as xs:string, $elem as node()) as node() {
  <item type="object">
    <pair name="text" type="string">
      { $elem/reb/text() }
    </pair>
    <pair name="tags" type="array">
      { for $info in $elem/re_inf
        return <item type="string"> { tags:convert-entity($word-id, $info/text()) } </item> }
    </pair>
    <pair name="appliesToKanji" type="array">
      { if (count($elem/re_restr) = 0)
        then <item type="string"> { "*" } </item>
        else for $restr in $elem/re_restr
          return <item type="string"> { $restr/text() } </item> }
    </pair>
  </item>
};

declare function transform:xref-part($xref-part as xs:string) as node() {
  if (number($xref-part))
  then <item type="number"> { number($xref-part) } </item>
  else <item type="string"> { $xref-part } </item>
};

declare function transform:xref($xref as node()) as node() {
  <item type="array">
    { for $xref-part in string:split($xref, "・")
      return transform:xref-part($xref-part) }
  </item>
};

declare function transform:inner-translation($trans as node()) as node() {
  <item type="object">
    <pair name="lang" type="string"> { $trans/@xml:lang/string() } </pair>
    <pair name="text" type="string"> { $trans/text() } </pair>
  </item>
};

declare function transform:translation($word-id as xs:string, $elem as node()) as node() {
  <item type="object">
    <pair name="type" type="array">
      { for $type in $elem/name_type
        return <item type="string"> { tags:convert-entity($word-id, $type/text()) } </item> }
    </pair>
    <pair name="related" type="array">
      { for $xref in $elem/xref
        return transform:xref($xref) }
    </pair>
    <pair name="translation" type="array">
      { for $trans in $elem/trans_det
        return transform:inner-translation($trans) }
    </pair>
  </item>
};

declare function transform:word($word as node()) as node() {
  let $word-id := $word/ent_seq/text()
  return <item type="object">
    <pair name="id" type="number">
      { $word-id }
    </pair>
    <pair name="kanji" type="array">
      { for $e in $word/k_ele
        return transform:kanji($word-id, $e) }
    </pair>
    <pair name="kana" type="array">
      { for $e in $word/r_ele
        return transform:kana($word-id, $e) }
    </pair>
    <pair name="translation" type="array">
      { for $e in $word/trans
        return transform:translation($word-id, $e) }
    </pair>
  </item>
};

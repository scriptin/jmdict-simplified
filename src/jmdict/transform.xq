xquery version "3.0";
module namespace transform = "transform";

import module namespace j = "http://www.w3.org/2005/xpath-functions";
import module namespace tags = "tags" at "tags.xq";

declare function transform:is-common($pri-elems as node()*) as xs:boolean {
  let $common-indicators := ("news1", "ichi1", "spec1", "spec2", "gai1")
  return if (exists($pri-elems[text() = $common-indicators]))
         then true()
         else false()
};

declare function transform:kanji($word-id as xs:string, $elem as node()) as node() {
  <j:map>
    <j:boolean key="common">
      { transform:is-common($elem/ke_pri) }
    </j:boolean>
    <j:string key="text">
      { $elem/keb/text() }
    </j:string>
    <j:array key="tags">
      { for $info in $elem/ke_inf
        return <j:string> { tags:convert-entity($word-id, $info/text()) } </j:string> }
    </j:array>
  </j:map>
};

declare function transform:kana($word-id as xs:string, $elem as node()) as node() {
  <j:map>
    <j:boolean key="common">
      { transform:is-common($elem/re_pri) }
    </j:boolean>
    <j:string key="text">
      { $elem/reb/text() }
    </j:string>
    <j:array key="tags">
      { for $info in $elem/re_inf
        return <j:string> { tags:convert-entity($word-id, $info/text()) } </j:string> }
    </j:array>
    <j:array key="appliesToKanji">
      { if ($elem/re_nokanji)
        then ()
        else if (count($elem/re_restr) = 0)
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

declare function transform:lsource($lsource as node()) as node() {
  <j:map>
    <j:string key="lang"> { $lsource/@xml:lang/string() } </j:string>
    <j:boolean key="full"> { ($lsource/@ls_type/string() = "full") } </j:boolean>
    <j:boolean key="wasei"> { ($lsource/@ls_wasei/string() = "y") } </j:boolean>
    { if ($lsource/text())
      then <j:string key="text"> { $lsource/text() } </j:string>
      else <j:null key="text"/> }
  </j:map>
};

declare function transform:g_type($word-id as xs:string, $g_type as attribute()?) as node() {
  if (not($g_type))
  then <j:null key="type"/>
  else switch($g_type)
    case "lit" return <j:string key="type"> { "literal" } </j:string>
    case "fig" return <j:string key="type"> { "figurative" } </j:string>
    case "expl" return <j:string key="type"> { "explanation" } </j:string>
    case "tm" return <j:string key="type"> { "trademark" } </j:string>
    default return error(
      xs:QName("unknown-gloss-type"),
      concat("Unknown gloss type '", $g_type, "' on entity ", $word-id)
    )
};

declare function transform:gloss($word-id as xs:string, $gloss as node()) as node() {
  <j:map>
    { transform:g_type($word-id, $gloss/@g_type) }
    <j:string key="lang"> { $gloss/@xml:lang/string() } </j:string>
    <j:string key="text"> { $gloss/text() } </j:string>
  </j:map>
};

(:
  Part-of-speech elements may not be present on certain sense elements:

  > In general where there are multiple senses
  > in an entry, the part-of-speech of an earlier sense will apply to
  > later senses unless there is a new part-of-speech indicated.

  (from the DOCTYPE of original dictionary file)

  To deal with this issue, this function accepts a subsequence of elements,
  starting from the first one, of length N, where N is the index of an element
  which is going to be transformed by this function. Other elements are used
  only to search for part-of-speech, if it's missing on the Nth element.
:)
declare function transform:sense($word-id as xs:string, $elems as node()*) as node() {
  let $elem := $elems[last()]
  let $last-pos := $elems[not(empty(*:pos))][last()]/pos
  return <j:map>
    <j:array key="partOfSpeech">
      { for $pos in $last-pos
        return <j:string> { tags:convert-entity($word-id, $pos/text()) } </j:string> }
    </j:array>
    <j:array key="appliesToKanji">
      { if (not($elem/stagk))
        then <j:string> { "*" } </j:string>
        else for $restr in $elem/stagk
          return <j:string> { $restr/text() } </j:string> }
    </j:array>
    <j:array key="appliesToKana">
      { if (not($elem/stagr))
        then <j:string> { "*" } </j:string>
        else for $restr in $elem/stagr
          return <j:string> { $restr/text() } </j:string> }
    </j:array>
    <j:array key="related">
      { for $xref in $elem/xref
        return transform:xref($xref) }
    </j:array>
    <j:array key="antonym">
      { for $ant in $elem/ant
        return transform:xref($ant) }
    </j:array>
    <j:array key="field">
      { for $field in $elem/field
        return <j:string> { tags:convert-entity($word-id, $field/text()) } </j:string> }
    </j:array>
    <j:array key="dialect">
      { for $dial in $elem/dial
        return <j:string> { tags:convert-entity($word-id, $dial/text()) } </j:string> }
    </j:array>
    <j:array key="misc">
      { for $misc in $elem/misc
        return <j:string> { tags:convert-entity($word-id, $misc/text()) } </j:string> }
    </j:array>
    <j:array key="info">
      { for $info in $elem/s_inf
        return <j:string> { $info/text() } </j:string> }
    </j:array>
    <j:array key="languageSource">
      { for $lsource in $elem/lsource
        return transform:lsource($lsource) }
    </j:array>
    <j:array key="gloss">
      { for $gloss in $elem/gloss
        return transform:gloss($word-id, $gloss) }
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
    <j:array key="sense">
      { for $idx in (1 to count($word/sense))
        return transform:sense($word-id, subsequence($word/sense, 1, $idx)) }
    </j:array>
  </j:map>
};

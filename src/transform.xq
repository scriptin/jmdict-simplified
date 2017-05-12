xquery version "3.0";
module namespace transform = "transform";

import module namespace string = "http://zorba.io/modules/string";
import module namespace tags = "tags" at "tags.xq";

(:
  Extract a creation date from a comment with following format: "JMdict created: YYYY-MM-DD".
:)
declare function transform:extract-date($doc as node()) as node() {
  let $version-comment := string($doc//comment()[contains(., 'JMdict created')][1])
  return <pair name="jmdict-date" type="string">
    { normalize-space(substring-after($version-comment, ':')) }
  </pair>
};

(:
  Extract revision numbers, as they appear in comments before DOCTYPE.
  Strictly speaking, these are version numbers of JMDict, but they are not mentioned in official documentation.
:)
declare function transform:extract-revisions($doc as node()) as node() {
  <pair name="jmdict-revisions" type="array">
    { for $rev in $doc//comment()[matches(., 'Rev \d+\.\d+')]
      let $first-line := substring-before(string($rev), '&#10;')
      return <item type="string"> { normalize-space(substring-after($first-line, 'Rev')) } </item> }
  </pair>
};

declare function transform:is-common($pri-elems as node()*) as xs:boolean {
  let $common-indicators := ("news1", "ichi1", "spec1", "spec2", "gai1")
  return if (exists($pri-elems[text() = $common-indicators]))
         then true()
         else false()
};

declare function transform:kanji($word-id as xs:string, $elem as node()) as node() {
  <item type="object">
    <pair name="common" type="boolean">
      { transform:is-common($elem/ke_pri) }
    </pair>
    <pair name="text" type="string">
      { $elem/keb/text() }
    </pair>
    <pair name="tags" type="array">
      { for $info in $elem/ke_inf
        return <item type="string"> { tags:convert-entity($word-id, $info/text()) } </item> }
    </pair>
  </item>
};

declare function transform:kana($word-id as xs:string, $elem as node()) as node() {
  <item type="object">
    <pair name="common" type="boolean">
      { transform:is-common($elem/re_pri) }
    </pair>
    <pair name="text" type="string">
      { $elem/reb/text() }
    </pair>
    <pair name="tags" type="array">
      { for $info in $elem/re_inf
        return <item type="string"> { tags:convert-entity($word-id, $info/text()) } </item> }
    </pair>
    <pair name="appliesToKanji" type="array">
      { if ($elem/re_nokanji)
        then ()
        else if (count($elem/re_restr) = 0)
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

declare function transform:lsource($lsource as node()) as node() {
  <item type="object">
    <pair name="lang" type="string"> { $lsource/@xml:lang/string() } </pair>
    <pair name="full" type="boolean"> { ($lsource/@ls_type/string() = "full") } </pair>
    <pair name="wasei" type="boolean"> { ($lsource/@ls_wasei/string() = "y") } </pair>
    { if ($lsource/text())
      then <pair name="text" type="string"> { $lsource/text() } </pair>
      else <pair name="text" type="null" /> }
  </item>
};

declare function transform:tranform-gloss($gloss as node()) as node() {
  <item type="object">
    <pair name="lang" type="string"> { $gloss/@xml:lang/string() } </pair>
    <pair name="text" type="string"> { $gloss/text() } </pair>
  </item>
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
  return <item type="object">
    <pair name="partOfSpeech" type="array">
      { for $pos in $last-pos
        return <item type="string"> { tags:convert-entity($word-id, $pos/text()) } </item> }
    </pair>
    <pair name="appliesToKanji" type="array">
      { if (not($elem/stagk))
        then <item type="string"> { "*" } </item>
        else for $restr in $elem/stagk
          return <item type="string"> { $restr/text() } </item> }
    </pair>
    <pair name="appliesToKana" type="array">
      { if (not($elem/stagr))
        then <item type="string"> { "*" } </item>
        else for $restr in $elem/stagr
          return <item type="string"> { $restr/text() } </item> }
    </pair>
    <pair name="related" type="array">
      { for $xref in $elem/xref
        return transform:xref($xref) }
    </pair>
    <pair name="antonym" type="array">
      { for $ant in $elem/ant
        return transform:xref($ant) }
    </pair>
    <pair name="field" type="array">
      { for $field in $elem/field
        return <item type="string"> { tags:convert-entity($word-id, $field/text()) } </item> }
    </pair>
    <pair name="dialect" type="array">
      { for $dial in $elem/dial
        return <item type="string"> { tags:convert-entity($word-id, $dial/text()) } </item> }
    </pair>
    <pair name="misc" type="array">
      { for $misc in $elem/misc
        return <item type="string"> { tags:convert-entity($word-id, $misc/text()) } </item> }
    </pair>
    <pair name="info" type="array">
      { for $info in $elem/s_inf
        return <item type="string"> { $info/text() } </item> }
    </pair>
    <pair name="languageSource" type="array">
      { for $lsource in $elem/lsource
        return transform:lsource($lsource) }
    </pair>
    <pair name="gloss" type="array">
      { for $gloss in $elem/gloss
        return transform:tranform-gloss($gloss) }
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
    <pair name="sense" type="array">
      { for $idx in (1 to count($word/sense))
        return transform:sense($word-id, subsequence($word/sense, 1, $idx)) }
    </pair>
  </item>
};

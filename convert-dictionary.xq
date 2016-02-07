xquery version "3.0";

import module namespace jx = "http://zorba.io/modules/json-xml";
import module namespace string = "http://zorba.io/modules/string";
import module namespace tags = "tags" at "tags.xq";

declare variable $doc external;

declare function local:is-common($pri-elems as node()*) as xs:boolean {
  let $common-indicators := ("news1", "ichi1", "spec1", "spec2", "gai1")
  return if (exists($pri-elems[text() = $common-indicators]))
         then true()
         else false()
};

declare function local:transform-kanji($elem as node()) as node() {
  <item type="object">
    <pair name="common" type="boolean">
      { local:is-common($elem/ke_pri) }
    </pair>
    <pair name="text" type="string">
      { $elem/keb/text() }
    </pair>
    <pair name="tags" type="array">
      { for $info in $elem/ke_inf
        return <item type="string"> { tags:convert-entity($info/text()) } </item> }
    </pair>
  </item>
};

declare function local:transform-kana($elem as node()) as node() {
  <item type="object">
    <pair name="common" type="boolean">
      { local:is-common($elem/re_pri) }
    </pair>
    <pair name="text" type="string">
      { $elem/reb/text() }
    </pair>
    <pair name="tags" type="array">
      { for $info in $elem/re_inf
        return <item type="string"> { tags:convert-entity($info/text()) } </item> }
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

declare function local:transform-xref-part($xref-part as xs:string) as node() {
  if (number($xref-part))
  then <item type="number"> { number($xref-part) } </item>
  else <item type="string"> { $xref-part } </item>
};

declare function local:transform-xref($xref as node()) as node() {
  <item type="array">
    { for $xref-part in string:split($xref, "・")
      return local:transform-xref-part($xref-part) }
  </item>
};

declare function local:transform-lsource($lsource as node()) as node() {
  <item type="object">
    <pair name="lang" type="string"> { $lsource/@xml:lang/string() } </pair>
    <pair name="full" type="boolean"> { ($lsource/@ls_type/string() = "full") } </pair>
    <pair name="wasei" type="boolean"> { ($lsource/@ls_wasei/string() = "y") } </pair>
    { if ($lsource/text())
      then <pair name="text" type="string"> { $lsource/text() } </pair>
      else <pair name="text" type="null" /> }
  </item>
};

declare function local:transform-sense($elem as node()) as node() {
  <item type="object">
    <pair name="partsOfSpeech" type="array">
      { for $pos in $elem/pos
        return <item type="string"> { tags:convert-entity($pos/text()) } </item> }
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
        return local:transform-xref($xref) }
    </pair>
    <pair name="antonyms" type="array">
      { for $ant in $elem/ant
        return local:transform-xref($ant) }
    </pair>
    <pair name="field" type="array">
      { for $field in $elem/field
        return <item type="string"> { tags:convert-entity($field/text()) } </item> }
    </pair>
    <pair name="misc" type="array">
      { for $misc in $elem/misc
        return <item type="string"> { tags:convert-entity($misc/text()) } </item> }
    </pair>
    <pair name="languageSource" type="array">
      { for $lsource in $elem/lsource
        return local:transform-lsource($lsource) }
    </pair>
    <pair name="dialect" type="array">
      { for $dial in $elem/dial
        return <item type="string"> { tags:convert-entity($dial/text()) } </item> }
    </pair>
    <pair name="gloss" type="array">
      { for $gloss in $elem/gloss
        return <item type="string"> { $gloss/text() } </item> }
    </pair>
  </item>
};

declare function local:transform-word($word as node()) as node() {
  <item type="object">
    <pair name="id" type="number">
      { $word/ent_seq/text() }
    </pair>
    <pair name="kanji" type="array">
      { for $e in $word/k_ele
        return local:transform-kanji($e) }
    </pair>
    <pair name="kana" type="array">
      { for $e in $word/r_ele
        return local:transform-kana($e) }
    </pair>
    <pair name="sense" type="array">
      { for $e in $word/sense
        return local:transform-sense($e) }
    </pair>
  </item>
};

jx:xml-to-json(
  <json type="object">
    <pair name="words" type="array">
      { for $word in $doc/JMdict/entry[position() >= 0 and position() < 2000]
        return local:transform-word($word) }
    </pair>
    { $tags:tags }
  </json>
)

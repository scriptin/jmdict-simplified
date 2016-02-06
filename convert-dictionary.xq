xquery version "3.0";

import module namespace jx = "http://zorba.io/modules/json-xml";

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
    <pair name="info" type="array">
      { for $info in $elem/ke_inf
        return <item type="string"> { local:convert-entity($info/text()) } </item> }
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
    <pair name="info" type="array">
      { for $info in $elem/re_inf
        return <item type="string"> { local:convert-entity($info/text()) } </item> }
    </pair>
    <pair name="appliesTo" type="array">
      { for $restr in $elem/re_restr
        return <item type="string"> { $restr/text() } </item> }
    </pair>
  </item>
};

declare function local:transform-sense($elem as node()) as node() {
  <item type="object">
    <pair name="pos" type="array">
      { for $pos in $elem/pos
        return <item type="string"> { local:convert-entity($pos/text()) } </item> }
    </pair>
    <pair name="gloss" type="array">
      { for $gloss in $elem/gloss
        return <item type="string"> { $gloss/text() } </item> }
    </pair>
  </item>
};

declare function local:convert-entity($text as xs:string) as xs:string? {
  switch($text)
  case "martial arts term" return "MA"
  case "rude or X-rated term (not displayed in educational software)" return "X"
  case "abbreviation" return "abbr"
  case "adjective (keiyoushi)" return "adj-i"
  case "adjective (keiyoushi) - yoi/ii class" return "adj-ix"
  case "adjectival nouns or quasi-adjectives (keiyodoshi)" return "adj-na"
  case "nouns which may take the genitive case particle `no'" return "adj-no"
  case "pre-noun adjectival (rentaishi)" return "adj-pn"
  case "`taru' adjective" return "adj-t"
  case "noun or verb acting prenominally" return "adj-f"
  case "adverb (fukushi)" return "adv"
  case "adverb taking the `to' particle" return "adv-to"
  case "archaism" return "arch"
  case "ateji (phonetic) reading" return "ateji"
  case "auxiliary" return "aux"
  case "auxiliary verb" return "aux-v"
  case "auxiliary adjective" return "aux-adj"
  case "Buddhist term" return "Buddh"
  case "chemistry term" return "chem"
  case "children's language" return "chn"
  case "colloquialism" return "col"
  case "computer terminology" return "comp"
  case "conjunction" return "conj"
  case "copula" return "cop-da"
  case "counter" return "ctr"
  case "derogatory" return "derog"
  case "exclusively kanji" return "eK"
  case "exclusively kana" return "ek"
  case "expressions (phrases, clauses, etc.)" return "exp"
  case "familiar language" return "fam"
  case "female term or language" return "fem"
  case "food term" return "food"
  case "geometry term" return "geom"
  case "gikun (meaning as reading) or jukujikun (special kanji reading)" return "gikun"
  case "honorific or respectful (sonkeigo) language" return "hon"
  case "humble (kenjougo) language" return "hum"
  case "word containing irregular kanji usage" return "iK"
  case "idiomatic expression" return "id"
  case "word containing irregular kana usage" return "ik"
  case "interjection (kandoushi)" return "int"
  case "irregular okurigana usage" return "io"
  case "irregular verb" return "iv"
  case "linguistics terminology" return "ling"
  case "manga slang" return "m-sl"
  case "male term or language" return "male"
  case "male slang" return "male-sl"
  case "mathematics" return "math"
  case "military" return "mil"
  case "noun (common) (futsuumeishi)" return "n"
  case "adverbial noun (fukushitekimeishi)" return "n-adv"
  case "noun, used as a suffix" return "n-suf"
  case "noun, used as a prefix" return "n-pref"
  case "noun (temporal) (jisoumeishi)" return "n-t"
  case "numeric" return "num"
  case "word containing out-dated kanji" return "oK"
  case "obsolete term" return "obs"
  case "obscure term" return "obsc"
  case "out-dated or obsolete kana usage" return "ok"
  case "old or irregular kana form" return "oik"
  case "onomatopoeic or mimetic word" return "on-mim"
  case "pronoun" return "pn"
  case "poetical term" return "poet"
  case "polite (teineigo) language" return "pol"
  case "prefix" return "pref"
  case "proverb" return "proverb"
  case "particle" return "prt"
  case "physics terminology" return "physics"
  case "rare" return "rare"
  case "sensitive" return "sens"
  case "slang" return "sl"
  case "suffix" return "suf"
  case "word usually written using kanji alone" return "uK"
  case "word usually written using kana alone" return "uk"
  case "unclassified" return "unc"
  case "yojijukugo" return "yoji"
  case "Ichidan verb" return "v1"
  case "Ichidan verb - kureru special class" return "v1-s"
  case "Nidan verb with 'u' ending (archaic)" return "v2a-s"
  case "Yodan verb with `hu/fu' ending (archaic)" return "v4h"
  case "Yodan verb with `ru' ending (archaic)" return "v4r"
  case "Godan verb - -aru special class" return "v5aru"
  case "Godan verb with `bu' ending" return "v5b"
  case "Godan verb with `gu' ending" return "v5g"
  case "Godan verb with `ku' ending" return "v5k"
  case "Godan verb - Iku/Yuku special class" return "v5k-s"
  case "Godan verb with `mu' ending" return "v5m"
  case "Godan verb with `nu' ending" return "v5n"
  case "Godan verb with `ru' ending" return "v5r"
  case "Godan verb with `ru' ending (irregular verb)" return "v5r-i"
  case "Godan verb with `su' ending" return "v5s"
  case "Godan verb with `tsu' ending" return "v5t"
  case "Godan verb with `u' ending" return "v5u"
  case "Godan verb with `u' ending (special class)" return "v5u-s"
  case "Godan verb - Uru old class verb (old form of Eru)" return "v5uru"
  case "Ichidan verb - zuru verb (alternative form of -jiru verbs)" return "vz"
  case "intransitive verb" return "vi"
  case "Kuru verb - special class" return "vk"
  case "irregular nu verb" return "vn"
  case "irregular ru verb, plain form ends with -ri" return "vr"
  case "noun or participle which takes the aux. verb suru" return "vs"
  case "su verb - precursor to the modern suru" return "vs-c"
  case "suru verb - special class" return "vs-s"
  case "suru verb - irregular" return "vs-i"
  case "Kyoto-ben" return "kyb"
  case "Osaka-ben" return "osb"
  case "Kansai-ben" return "ksb"
  case "Kantou-ben" return "ktb"
  case "Tosa-ben" return "tsb"
  case "Touhoku-ben" return "thb"
  case "Tsugaru-ben" return "tsug"
  case "Kyuushuu-ben" return "kyu"
  case "Ryuukyuu-ben" return "rkb"
  case "Nagano-ben" return "nab"
  case "Hokkaido-ben" return "hob"
  case "transitive verb" return "vt"
  case "vulgar expression or word" return "vulg"
  case "`kari' adjective (archaic)" return "adj-kari"
  case "`ku' adjective (archaic)" return "adj-ku"
  case "`shiku' adjective (archaic)" return "adj-shiku"
  case "archaic/formal form of na-adjective" return "adj-nari"
  case "proper noun" return "n-pr"
  case "verb unspecified" return "v-unspec"
  case "Yodan verb with `ku' ending (archaic)" return "v4k"
  case "Yodan verb with `gu' ending (archaic)" return "v4g"
  case "Yodan verb with `su' ending (archaic)" return "v4s"
  case "Yodan verb with `tsu' ending (archaic)" return "v4t"
  case "Yodan verb with `nu' ending (archaic)" return "v4n"
  case "Yodan verb with `bu' ending (archaic)" return "v4b"
  case "Yodan verb with `mu' ending (archaic)" return "v4m"
  case "Nidan verb (upper class) with `ku' ending (archaic)" return "v2k-k"
  case "Nidan verb (upper class) with `gu' ending (archaic)" return "v2g-k"
  case "Nidan verb (upper class) with `tsu' ending (archaic)" return "v2t-k"
  case "Nidan verb (upper class) with `dzu' ending (archaic)" return "v2d-k"
  case "Nidan verb (upper class) with `hu/fu' ending (archaic)" return "v2h-k"
  case "Nidan verb (upper class) with `bu' ending (archaic)" return "v2b-k"
  case "Nidan verb (upper class) with `mu' ending (archaic)" return "v2m-k"
  case "Nidan verb (upper class) with `yu' ending (archaic)" return "v2y-k"
  case "Nidan verb (upper class) with `ru' ending (archaic)" return "v2r-k"
  case "Nidan verb (lower class) with `ku' ending (archaic)" return "v2k-s"
  case "Nidan verb (lower class) with `gu' ending (archaic)" return "v2g-s"
  case "Nidan verb (lower class) with `su' ending (archaic)" return "v2s-s"
  case "Nidan verb (lower class) with `zu' ending (archaic)" return "v2z-s"
  case "Nidan verb (lower class) with `tsu' ending (archaic)" return "v2t-s"
  case "Nidan verb (lower class) with `dzu' ending (archaic)" return "v2d-s"
  case "Nidan verb (lower class) with `nu' ending (archaic)" return "v2n-s"
  case "Nidan verb (lower class) with `hu/fu' ending (archaic)" return "v2h-s"
  case "Nidan verb (lower class) with `bu' ending (archaic)" return "v2b-s"
  case "Nidan verb (lower class) with `mu' ending (archaic)" return "v2m-s"
  case "Nidan verb (lower class) with `yu' ending (archaic)" return "v2y-s"
  case "Nidan verb (lower class) with `ru' ending (archaic)" return "v2r-s"
  case "Nidan verb (lower class) with `u' ending and `we' conjugation (archaic)" return "v2w-s"
  case "architecture term" return "archit"
  case "astronomy, etc. term" return "astron"
  case "baseball term" return "baseb"
  case "biology term" return "biol"
  case "botany term" return "bot"
  case "business term" return "bus"
  case "economics term" return "econ"
  case "engineering term" return "engr"
  case "finance term" return "finc"
  case "geology, etc. term" return "geol"
  case "law, etc. term" return "law"
  case "mahjong term" return "mahj"
  case "medicine, etc. term" return "med"
  case "music term" return "music"
  case "Shinto term" return "Shinto"
  case "shogi term" return "shogi"
  case "sports term" return "sports"
  case "sumo term" return "sumo"
  case "zoology term" return "zool"
  case "jocular, humorous term" return "joc"
  case "anatomical term" return "anat"
  default return "<UNKNOWN_ENTITY>"
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

declare variable $parts-of-speech :=
  <pair name="partsOfSpeech" type="object">
    <pair name="adj-i" type="string">adjective</pair>
    <pair name="adj-na" type="string">adjectival nouns or quasi-adjectives</pair>
    <pair name="adj-no" type="string">nouns which may take the genitive case particle 'no'</pair>
    <pair name="adj-pn" type="string">pre-noun adjectival</pair>
    <pair name="adj-t" type="string">'taru' adjective</pair>
    <pair name="adj-f" type="string">noun or verb acting prenominally</pair>
    <pair name="adj" type="string">unclassified adjective</pair>
    <pair name="adv" type="string">adverb</pair>
    <pair name="adv-n" type="string">adverbial noun</pair>
    <pair name="adv-to" type="string">adverb taking the 'to' particle</pair>
    <pair name="aux" type="string">auxiliary</pair>
    <pair name="aux-v" type="string">auxiliary verb</pair>
    <pair name="aux-adj" type="string">auxiliary adjective</pair>
    <pair name="conj" type="string">conjunction</pair>
    <pair name="ctr" type="string">counter</pair>
    <pair name="exp" type="string">expression (phrase, clause, etc.)</pair>
    <pair name="int" type="string">interjection</pair>
    <pair name="iv" type="string">irregular verb</pair>
    <pair name="n" type="string">noun (common)</pair>
    <pair name="n-adv" type="string">adverbial noun</pair>
    <pair name="n-pref" type="string">noun, used as a prefix</pair>
    <pair name="n-suf" type="string">noun, used as a suffix</pair>
    <pair name="n-t" type="string">noun (temporal)</pair>
    <pair name="num" type="string">numeric</pair>
    <pair name="pn" type="string">pronoun</pair>
    <pair name="pref" type="string">prefix</pair>
    <pair name="prt" type="string">particle</pair>
    <pair name="suf" type="string">suffix</pair>
    <pair name="v1" type="string">ichidan verb, 'ru' verb</pair>
    <pair name="v2a-s" type="string">nidan verb with 'u' ending (archaic)</pair>
    <pair name="v4h" type="string">yodan verb with 'hu'/'fu' ending (archaic)</pair>
    <pair name="v4r" type="string">yodan verb with 'ru' ending (archaic)</pair>
    <pair name="v5" type="string">godan verb (unclassified)</pair>
    <pair name="v5aru" type="string">godan verb, -aru special class</pair>
    <pair name="v5b" type="string">godan verb with 'bu' ending</pair>
    <pair name="v5g" type="string">godan verb with 'gu' ending</pair>
    <pair name="v5k" type="string">godan verb with 'ku' ending</pair>
    <pair name="v5k-s" type="string">godan verb, 'iku'/'yuku' special class</pair>
    <pair name="v5m" type="string">godan verb with 'mu' ending</pair>
    <pair name="v5n" type="string">godan verb with 'nu' ending</pair>
    <pair name="v5r" type="string">godan verb with 'ru' ending</pair>
    <pair name="v5r-i" type="string">godan verb with 'ru' ending (irregular verb)</pair>
    <pair name="v5s" type="string">godan verb with 'su' ending</pair>
    <pair name="v5t" type="string">godan verb with 'tsu' ending</pair>
    <pair name="v5u" type="string">godan verb with 'u' ending</pair>
    <pair name="v5u-s" type="string">godan verb with 'u' ending (special class)</pair>
    <pair name="v5uru" type="string">godan verb, 'uru' old class verb (old form of 'eru')</pair>
    <pair name="v5z" type="string">godan verb with 'zu' ending</pair>
    <pair name="vz" type="string">ichidan verb, 'zuru' verb (alternative form of 'jiru' verbs)</pair>
    <pair name="vi" type="string">intransitive verb</pair>
    <pair name="vk" type="string">'kuru' verb, special class</pair>
    <pair name="vn" type="string">irregular 'nu' verb</pair>
    <pair name="vs" type="string">noun or participle which takes the aux. verb 'suru'</pair>
    <pair name="vs-c" type="string">'su' verb, precursor to the modern 'suru'</pair>
    <pair name="vs-i" type="string">'suru' verb, irregular</pair>
    <pair name="vs-s" type="string">'suru' verb, special class</pair>
    <pair name="vt" type="string">transitive verb</pair>
  </pair>;

jx:xml-to-json(
  <json type="object">
    { $parts-of-speech }
    <pair name="words" type="array">
      { for $word in $doc/JMdict/entry[position() > 30 and position() < 150]
        return local:transform-word($word) }
    </pair>
  </json>
)

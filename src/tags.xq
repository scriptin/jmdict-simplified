xquery version "3.0";
module namespace tags = "tags";

declare function tags:convert-entity($word-id as xs:string, $text as xs:string) as xs:string? {
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
  default return error(xs:QName('broken-tag'), concat("Unknown tag '", $text, "' on entity ", $word-id))
};

declare variable $tags:tags := <pair name="tags" type="object">
  <pair name="MA" type="string">martial arts term</pair>
  <pair name="X" type="string">rude or X-rated term</pair>
  <pair name="abbr" type="string">abbreviation</pair>
  <pair name="adj-i" type="string">adjective (keiyoushi)</pair>
  <pair name="adj-ix" type="string">adjective (keiyoushi) - yoi/ii class</pair>
  <pair name="adj-na" type="string">adjectival nouns or quasi-adjectives (keiyodoshi)</pair>
  <pair name="adj-no" type="string">nouns which may take the genitive case particle &apos;no&apos;</pair>
  <pair name="adj-pn" type="string">pre-noun adjectival (rentaishi)</pair>
  <pair name="adj-t" type="string">&apos;taru&apos; adjective</pair>
  <pair name="adj-f" type="string">noun or verb acting prenominally</pair>
  <pair name="adv" type="string">adverb (fukushi)</pair>
  <pair name="adv-to" type="string">adverb taking the &apos;to&apos; particle</pair>
  <pair name="arch" type="string">archaism</pair>
  <pair name="ateji" type="string">ateji (phonetic) reading</pair>
  <pair name="aux" type="string">auxiliary</pair>
  <pair name="aux-v" type="string">auxiliary verb</pair>
  <pair name="aux-adj" type="string">auxiliary adjective</pair>
  <pair name="Buddh" type="string">buddhist term</pair>
  <pair name="chem" type="string">chemistry term</pair>
  <pair name="chn" type="string">children&apos;s language</pair>
  <pair name="col" type="string">colloquialism</pair>
  <pair name="comp" type="string">computer terminology</pair>
  <pair name="conj" type="string">conjunction</pair>
  <pair name="cop-da" type="string">copula</pair>
  <pair name="ctr" type="string">counter</pair>
  <pair name="derog" type="string">derogatory</pair>
  <pair name="eK" type="string">exclusively kanji</pair>
  <pair name="ek" type="string">exclusively kana</pair>
  <pair name="exp" type="string">expressions (phrases, clauses, etc.)</pair>
  <pair name="fam" type="string">familiar language</pair>
  <pair name="fem" type="string">female term or language</pair>
  <pair name="food" type="string">food term</pair>
  <pair name="geom" type="string">geometry term</pair>
  <pair name="gikun" type="string">gikun (meaning as reading) or jukujikun (special kanji reading)</pair>
  <pair name="hon" type="string">honorific or respectful (sonkeigo) language</pair>
  <pair name="hum" type="string">humble (kenjougo) language</pair>
  <pair name="iK" type="string">word containing irregular kanji usage</pair>
  <pair name="id" type="string">idiomatic expression</pair>
  <pair name="ik" type="string">word containing irregular kana usage</pair>
  <pair name="int" type="string">interjection (kandoushi)</pair>
  <pair name="io" type="string">irregular okurigana usage</pair>
  <pair name="iv" type="string">irregular verb</pair>
  <pair name="ling" type="string">linguistics terminology</pair>
  <pair name="m-sl" type="string">manga slang</pair>
  <pair name="male" type="string">male term or language</pair>
  <pair name="male-sl" type="string">male slang</pair>
  <pair name="math" type="string">mathematics</pair>
  <pair name="mil" type="string">military</pair>
  <pair name="n" type="string">noun (common) (futsuumeishi)</pair>
  <pair name="n-adv" type="string">adverbial noun (fukushitekimeishi)</pair>
  <pair name="n-suf" type="string">noun, used as a suffix</pair>
  <pair name="n-pref" type="string">noun, used as a prefix</pair>
  <pair name="n-t" type="string">noun (temporal) (jisoumeishi)</pair>
  <pair name="num" type="string">numeric</pair>
  <pair name="oK" type="string">word containing out-dated kanji</pair>
  <pair name="obs" type="string">obsolete term</pair>
  <pair name="obsc" type="string">obscure term</pair>
  <pair name="ok" type="string">out-dated or obsolete kana usage</pair>
  <pair name="oik" type="string">old or irregular kana form</pair>
  <pair name="on-mim" type="string">onomatopoeic or mimetic word</pair>
  <pair name="pn" type="string">pronoun</pair>
  <pair name="poet" type="string">poetical term</pair>
  <pair name="pol" type="string">polite (teineigo) language</pair>
  <pair name="pref" type="string">prefix</pair>
  <pair name="proverb" type="string">proverb</pair>
  <pair name="prt" type="string">particle</pair>
  <pair name="physics" type="string">physics terminology</pair>
  <pair name="rare" type="string">rare</pair>
  <pair name="sens" type="string">sensitive</pair>
  <pair name="sl" type="string">slang</pair>
  <pair name="suf" type="string">suffix</pair>
  <pair name="uK" type="string">word usually written using kanji alone</pair>
  <pair name="uk" type="string">word usually written using kana alone</pair>
  <pair name="unc" type="string">unclassified</pair>
  <pair name="yoji" type="string">yojijukugo</pair>
  <pair name="v1" type="string">ichidan verb</pair>
  <pair name="v1-s" type="string">ichidan verb - &apos;kureru&apos; special class</pair>
  <pair name="v2a-s" type="string">nidan verb with &apos;u&apos; ending (archaic)</pair>
  <pair name="v4h" type="string">yodan verb with &apos;hu/fu&apos; ending (archaic)</pair>
  <pair name="v4r" type="string">yodan verb with &apos;ru&apos; ending (archaic)</pair>
  <pair name="v5aru" type="string">godan verb - &apos;aru&apos; special class</pair>
  <pair name="v5b" type="string">godan verb with &apos;bu&apos; ending</pair>
  <pair name="v5g" type="string">godan verb with &apos;gu&apos; ending</pair>
  <pair name="v5k" type="string">godan verb with &apos;ku&apos; ending</pair>
  <pair name="v5k-s" type="string">godan verb - &apos;iku/yuku&apos; special class</pair>
  <pair name="v5m" type="string">godan verb with &apos;mu&apos; ending</pair>
  <pair name="v5n" type="string">godan verb with &apos;nu&apos; ending</pair>
  <pair name="v5r" type="string">godan verb with &apos;ru&apos; ending</pair>
  <pair name="v5r-i" type="string">godan verb with &apos;ru&apos; ending (irregular verb)</pair>
  <pair name="v5s" type="string">godan verb with &apos;su&apos; ending</pair>
  <pair name="v5t" type="string">godan verb with &apos;tsu&apos; ending</pair>
  <pair name="v5u" type="string">godan verb with &apos;u&apos; ending</pair>
  <pair name="v5u-s" type="string">godan verb with &apos;u&apos; ending (special class)</pair>
  <pair name="v5uru" type="string">godan verb - &apos;uru&apos; old class verb (old form of &apos;eru&apos;)</pair>
  <pair name="vz" type="string">ichidan verb - &apos;zuru&apos; verb (alternative form of &apos;jiru&apos; verbs)</pair>
  <pair name="vi" type="string">intransitive verb</pair>
  <pair name="vk" type="string">&apos;kuru&apos; verb - special class</pair>
  <pair name="vn" type="string">irregular &apos;nu&apos; verb</pair>
  <pair name="vr" type="string">irregular &apos;ru&apos; verb, plain form ends with &apos;ri&apos;</pair>
  <pair name="vs" type="string">noun or participle which takes the aux. verb &apos;suru&apos;</pair>
  <pair name="vs-c" type="string">&apos;su&apos; verb - precursor to the modern &apos;suru&apos;</pair>
  <pair name="vs-s" type="string">&apos;suru&apos; verb - special class</pair>
  <pair name="vs-i" type="string">&apos;suru&apos; verb - irregular</pair>
  <pair name="kyb" type="string">Kyoto-ben</pair>
  <pair name="osb" type="string">Osaka-ben</pair>
  <pair name="ksb" type="string">Kansai-ben</pair>
  <pair name="ktb" type="string">Kantou-ben</pair>
  <pair name="tsb" type="string">Tosa-ben</pair>
  <pair name="thb" type="string">Touhoku-ben</pair>
  <pair name="tsug" type="string">Tsugaru-ben</pair>
  <pair name="kyu" type="string">Kyuushuu-ben</pair>
  <pair name="rkb" type="string">Ryuukyuu-ben</pair>
  <pair name="nab" type="string">Nagano-ben</pair>
  <pair name="hob" type="string">Hokkaido-ben</pair>
  <pair name="vt" type="string">transitive verb</pair>
  <pair name="vulg" type="string">vulgar expression or word</pair>
  <pair name="adj-kari" type="string">&apos;kari&apos; adjective (archaic)</pair>
  <pair name="adj-ku" type="string">&apos;ku&apos; adjective (archaic)</pair>
  <pair name="adj-shiku" type="string">&apos;shiku&apos; adjective (archaic)</pair>
  <pair name="adj-nari" type="string">archaic/formal form of na-adjective</pair>
  <pair name="n-pr" type="string">proper noun</pair>
  <pair name="v-unspec" type="string">verb unspecified</pair>
  <pair name="v4k" type="string">yodan verb with &apos;ku&apos; ending (archaic)</pair>
  <pair name="v4g" type="string">yodan verb with &apos;gu&apos; ending (archaic)</pair>
  <pair name="v4s" type="string">yodan verb with &apos;su&apos; ending (archaic)</pair>
  <pair name="v4t" type="string">yodan verb with &apos;tsu&apos; ending (archaic)</pair>
  <pair name="v4n" type="string">yodan verb with &apos;nu&apos; ending (archaic)</pair>
  <pair name="v4b" type="string">yodan verb with &apos;bu&apos; ending (archaic)</pair>
  <pair name="v4m" type="string">yodan verb with &apos;mu&apos; ending (archaic)</pair>
  <pair name="v2k-k" type="string">nidan verb (upper class) with &apos;ku&apos; ending (archaic)</pair>
  <pair name="v2g-k" type="string">nidan verb (upper class) with &apos;gu&apos; ending (archaic)</pair>
  <pair name="v2t-k" type="string">nidan verb (upper class) with &apos;tsu&apos; ending (archaic)</pair>
  <pair name="v2d-k" type="string">nidan verb (upper class) with &apos;dzu&apos; ending (archaic)</pair>
  <pair name="v2h-k" type="string">nidan verb (upper class) with &apos;hu/fu&apos; ending (archaic)</pair>
  <pair name="v2b-k" type="string">nidan verb (upper class) with &apos;bu&apos; ending (archaic)</pair>
  <pair name="v2m-k" type="string">nidan verb (upper class) with &apos;mu&apos; ending (archaic)</pair>
  <pair name="v2y-k" type="string">nidan verb (upper class) with &apos;yu&apos; ending (archaic)</pair>
  <pair name="v2r-k" type="string">nidan verb (upper class) with &apos;ru&apos; ending (archaic)</pair>
  <pair name="v2k-s" type="string">nidan verb (lower class) with &apos;ku&apos; ending (archaic)</pair>
  <pair name="v2g-s" type="string">nidan verb (lower class) with &apos;gu&apos; ending (archaic)</pair>
  <pair name="v2s-s" type="string">nidan verb (lower class) with &apos;su&apos; ending (archaic)</pair>
  <pair name="v2z-s" type="string">nidan verb (lower class) with &apos;zu&apos; ending (archaic)</pair>
  <pair name="v2t-s" type="string">nidan verb (lower class) with &apos;tsu&apos; ending (archaic)</pair>
  <pair name="v2d-s" type="string">nidan verb (lower class) with &apos;dzu&apos; ending (archaic)</pair>
  <pair name="v2n-s" type="string">nidan verb (lower class) with &apos;nu&apos; ending (archaic)</pair>
  <pair name="v2h-s" type="string">nidan verb (lower class) with &apos;hu/fu&apos; ending (archaic)</pair>
  <pair name="v2b-s" type="string">nidan verb (lower class) with &apos;bu&apos; ending (archaic)</pair>
  <pair name="v2m-s" type="string">nidan verb (lower class) with &apos;mu&apos; ending (archaic)</pair>
  <pair name="v2y-s" type="string">nidan verb (lower class) with &apos;yu&apos; ending (archaic)</pair>
  <pair name="v2r-s" type="string">nidan verb (lower class) with &apos;ru&apos; ending (archaic)</pair>
  <pair name="v2w-s" type="string">nidan verb (lower class) with &apos;u&apos; ending and &apos;we&apos; conjugation (archaic)</pair>
  <pair name="archit" type="string">architecture term</pair>
  <pair name="astron" type="string">astronomy, etc. term</pair>
  <pair name="baseb" type="string">baseball term</pair>
  <pair name="biol" type="string">biology term</pair>
  <pair name="bot" type="string">botany term</pair>
  <pair name="bus" type="string">business term</pair>
  <pair name="econ" type="string">economics term</pair>
  <pair name="engr" type="string">engineering term</pair>
  <pair name="finc" type="string">finance term</pair>
  <pair name="geol" type="string">geology, etc. term</pair>
  <pair name="law" type="string">law, etc. term</pair>
  <pair name="mahj" type="string">mahjong term</pair>
  <pair name="med" type="string">medicine, etc. term</pair>
  <pair name="music" type="string">music term</pair>
  <pair name="Shinto" type="string">Shinto term</pair>
  <pair name="shogi" type="string">shogi term</pair>
  <pair name="sports" type="string">sports term</pair>
  <pair name="sumo" type="string">sumo term</pair>
  <pair name="zool" type="string">zoology term</pair>
  <pair name="joc" type="string">jocular, humorous term</pair>
  <pair name="anat" type="string">anatomical term</pair>
</pair>;

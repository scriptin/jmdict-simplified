xquery version "3.0";
module namespace tags = "tags";

import module namespace j = "http://www.w3.org/2005/xpath-functions";

(: This file is generated, do not edit manually! :)

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
  case "copula" return "cop"
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
  case "quotation" return "quote"
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
  case "suru verb - included" return "vs-i"
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
  case "Christian term" return "Christn"
  case "Internet slang" return "net-sl"
  case "dated term" return "dated"
  case "historical term" return "hist"
  case "literary or formal term" return "litf"
  case "family or surname" return "surname"
  case "place name" return "place"
  case "unclassified name" return "unclass"
  case "company name" return "company"
  case "product name" return "product"
  case "work of art, literature, music, etc. name" return "work"
  case "full name of a particular person" return "person"
  case "given name or forename, gender not specified" return "given"
  case "railway station" return "station"
  case "organization name" return "organization"
  default return error(
    xs:QName("unknown-tag"),
    concat("Unknown tag '", $text, "' on entity ", $word-id)
  )
};

declare variable $tags:tags := <j:map key="tags">
  <j:string key="MA">martial arts term</j:string>
  <j:string key="X">rude or X-rated term (not displayed in educational software)</j:string>
  <j:string key="abbr">abbreviation</j:string>
  <j:string key="adj-i">adjective (keiyoushi)</j:string>
  <j:string key="adj-ix">adjective (keiyoushi) - yoi/ii class</j:string>
  <j:string key="adj-na">adjectival nouns or quasi-adjectives (keiyodoshi)</j:string>
  <j:string key="adj-no">nouns which may take the genitive case particle &apos;no&apos;</j:string>
  <j:string key="adj-pn">pre-noun adjectival (rentaishi)</j:string>
  <j:string key="adj-t">&apos;taru&apos; adjective</j:string>
  <j:string key="adj-f">noun or verb acting prenominally</j:string>
  <j:string key="adv">adverb (fukushi)</j:string>
  <j:string key="adv-to">adverb taking the &apos;to&apos; particle</j:string>
  <j:string key="arch">archaism</j:string>
  <j:string key="ateji">ateji (phonetic) reading</j:string>
  <j:string key="aux">auxiliary</j:string>
  <j:string key="aux-v">auxiliary verb</j:string>
  <j:string key="aux-adj">auxiliary adjective</j:string>
  <j:string key="Buddh">Buddhist term</j:string>
  <j:string key="chem">chemistry term</j:string>
  <j:string key="chn">children&apos;s language</j:string>
  <j:string key="col">colloquialism</j:string>
  <j:string key="comp">computer terminology</j:string>
  <j:string key="conj">conjunction</j:string>
  <j:string key="cop">copula</j:string>
  <j:string key="ctr">counter</j:string>
  <j:string key="derog">derogatory</j:string>
  <j:string key="eK">exclusively kanji</j:string>
  <j:string key="ek">exclusively kana</j:string>
  <j:string key="exp">expressions (phrases, clauses, etc.)</j:string>
  <j:string key="fam">familiar language</j:string>
  <j:string key="fem">female term or language</j:string>
  <j:string key="food">food term</j:string>
  <j:string key="geom">geometry term</j:string>
  <j:string key="gikun">gikun (meaning as reading) or jukujikun (special kanji reading)</j:string>
  <j:string key="hon">honorific or respectful (sonkeigo) language</j:string>
  <j:string key="hum">humble (kenjougo) language</j:string>
  <j:string key="iK">word containing irregular kanji usage</j:string>
  <j:string key="id">idiomatic expression</j:string>
  <j:string key="ik">word containing irregular kana usage</j:string>
  <j:string key="int">interjection (kandoushi)</j:string>
  <j:string key="io">irregular okurigana usage</j:string>
  <j:string key="iv">irregular verb</j:string>
  <j:string key="ling">linguistics terminology</j:string>
  <j:string key="m-sl">manga slang</j:string>
  <j:string key="male">male term or language</j:string>
  <j:string key="male-sl">male slang</j:string>
  <j:string key="math">mathematics</j:string>
  <j:string key="mil">military</j:string>
  <j:string key="n">noun (common) (futsuumeishi)</j:string>
  <j:string key="n-adv">adverbial noun (fukushitekimeishi)</j:string>
  <j:string key="n-suf">noun, used as a suffix</j:string>
  <j:string key="n-pref">noun, used as a prefix</j:string>
  <j:string key="n-t">noun (temporal) (jisoumeishi)</j:string>
  <j:string key="num">numeric</j:string>
  <j:string key="oK">word containing out-dated kanji</j:string>
  <j:string key="obs">obsolete term</j:string>
  <j:string key="obsc">obscure term</j:string>
  <j:string key="ok">out-dated or obsolete kana usage</j:string>
  <j:string key="oik">old or irregular kana form</j:string>
  <j:string key="on-mim">onomatopoeic or mimetic word</j:string>
  <j:string key="pn">pronoun</j:string>
  <j:string key="poet">poetical term</j:string>
  <j:string key="pol">polite (teineigo) language</j:string>
  <j:string key="pref">prefix</j:string>
  <j:string key="proverb">proverb</j:string>
  <j:string key="prt">particle</j:string>
  <j:string key="physics">physics terminology</j:string>
  <j:string key="quote">quotation</j:string>
  <j:string key="rare">rare</j:string>
  <j:string key="sens">sensitive</j:string>
  <j:string key="sl">slang</j:string>
  <j:string key="suf">suffix</j:string>
  <j:string key="uK">word usually written using kanji alone</j:string>
  <j:string key="uk">word usually written using kana alone</j:string>
  <j:string key="unc">unclassified</j:string>
  <j:string key="yoji">yojijukugo</j:string>
  <j:string key="v1">Ichidan verb</j:string>
  <j:string key="v1-s">Ichidan verb - kureru special class</j:string>
  <j:string key="v2a-s">Nidan verb with &apos;u&apos; ending (archaic)</j:string>
  <j:string key="v4h">Yodan verb with &apos;hu/fu&apos; ending (archaic)</j:string>
  <j:string key="v4r">Yodan verb with &apos;ru&apos; ending (archaic)</j:string>
  <j:string key="v5aru">Godan verb - -aru special class</j:string>
  <j:string key="v5b">Godan verb with &apos;bu&apos; ending</j:string>
  <j:string key="v5g">Godan verb with &apos;gu&apos; ending</j:string>
  <j:string key="v5k">Godan verb with &apos;ku&apos; ending</j:string>
  <j:string key="v5k-s">Godan verb - Iku/Yuku special class</j:string>
  <j:string key="v5m">Godan verb with &apos;mu&apos; ending</j:string>
  <j:string key="v5n">Godan verb with &apos;nu&apos; ending</j:string>
  <j:string key="v5r">Godan verb with &apos;ru&apos; ending</j:string>
  <j:string key="v5r-i">Godan verb with &apos;ru&apos; ending (irregular verb)</j:string>
  <j:string key="v5s">Godan verb with &apos;su&apos; ending</j:string>
  <j:string key="v5t">Godan verb with &apos;tsu&apos; ending</j:string>
  <j:string key="v5u">Godan verb with &apos;u&apos; ending</j:string>
  <j:string key="v5u-s">Godan verb with &apos;u&apos; ending (special class)</j:string>
  <j:string key="v5uru">Godan verb - Uru old class verb (old form of Eru)</j:string>
  <j:string key="vz">Ichidan verb - zuru verb (alternative form of -jiru verbs)</j:string>
  <j:string key="vi">intransitive verb</j:string>
  <j:string key="vk">Kuru verb - special class</j:string>
  <j:string key="vn">irregular nu verb</j:string>
  <j:string key="vr">irregular ru verb, plain form ends with -ri</j:string>
  <j:string key="vs">noun or participle which takes the aux. verb suru</j:string>
  <j:string key="vs-c">su verb - precursor to the modern suru</j:string>
  <j:string key="vs-s">suru verb - special class</j:string>
  <j:string key="vs-i">suru verb - included</j:string>
  <j:string key="kyb">Kyoto-ben</j:string>
  <j:string key="osb">Osaka-ben</j:string>
  <j:string key="ksb">Kansai-ben</j:string>
  <j:string key="ktb">Kantou-ben</j:string>
  <j:string key="tsb">Tosa-ben</j:string>
  <j:string key="thb">Touhoku-ben</j:string>
  <j:string key="tsug">Tsugaru-ben</j:string>
  <j:string key="kyu">Kyuushuu-ben</j:string>
  <j:string key="rkb">Ryuukyuu-ben</j:string>
  <j:string key="nab">Nagano-ben</j:string>
  <j:string key="hob">Hokkaido-ben</j:string>
  <j:string key="vt">transitive verb</j:string>
  <j:string key="vulg">vulgar expression or word</j:string>
  <j:string key="adj-kari">&apos;kari&apos; adjective (archaic)</j:string>
  <j:string key="adj-ku">&apos;ku&apos; adjective (archaic)</j:string>
  <j:string key="adj-shiku">&apos;shiku&apos; adjective (archaic)</j:string>
  <j:string key="adj-nari">archaic/formal form of na-adjective</j:string>
  <j:string key="n-pr">proper noun</j:string>
  <j:string key="v-unspec">verb unspecified</j:string>
  <j:string key="v4k">Yodan verb with &apos;ku&apos; ending (archaic)</j:string>
  <j:string key="v4g">Yodan verb with &apos;gu&apos; ending (archaic)</j:string>
  <j:string key="v4s">Yodan verb with &apos;su&apos; ending (archaic)</j:string>
  <j:string key="v4t">Yodan verb with &apos;tsu&apos; ending (archaic)</j:string>
  <j:string key="v4n">Yodan verb with &apos;nu&apos; ending (archaic)</j:string>
  <j:string key="v4b">Yodan verb with &apos;bu&apos; ending (archaic)</j:string>
  <j:string key="v4m">Yodan verb with &apos;mu&apos; ending (archaic)</j:string>
  <j:string key="v2k-k">Nidan verb (upper class) with &apos;ku&apos; ending (archaic)</j:string>
  <j:string key="v2g-k">Nidan verb (upper class) with &apos;gu&apos; ending (archaic)</j:string>
  <j:string key="v2t-k">Nidan verb (upper class) with &apos;tsu&apos; ending (archaic)</j:string>
  <j:string key="v2d-k">Nidan verb (upper class) with &apos;dzu&apos; ending (archaic)</j:string>
  <j:string key="v2h-k">Nidan verb (upper class) with &apos;hu/fu&apos; ending (archaic)</j:string>
  <j:string key="v2b-k">Nidan verb (upper class) with &apos;bu&apos; ending (archaic)</j:string>
  <j:string key="v2m-k">Nidan verb (upper class) with &apos;mu&apos; ending (archaic)</j:string>
  <j:string key="v2y-k">Nidan verb (upper class) with &apos;yu&apos; ending (archaic)</j:string>
  <j:string key="v2r-k">Nidan verb (upper class) with &apos;ru&apos; ending (archaic)</j:string>
  <j:string key="v2k-s">Nidan verb (lower class) with &apos;ku&apos; ending (archaic)</j:string>
  <j:string key="v2g-s">Nidan verb (lower class) with &apos;gu&apos; ending (archaic)</j:string>
  <j:string key="v2s-s">Nidan verb (lower class) with &apos;su&apos; ending (archaic)</j:string>
  <j:string key="v2z-s">Nidan verb (lower class) with &apos;zu&apos; ending (archaic)</j:string>
  <j:string key="v2t-s">Nidan verb (lower class) with &apos;tsu&apos; ending (archaic)</j:string>
  <j:string key="v2d-s">Nidan verb (lower class) with &apos;dzu&apos; ending (archaic)</j:string>
  <j:string key="v2n-s">Nidan verb (lower class) with &apos;nu&apos; ending (archaic)</j:string>
  <j:string key="v2h-s">Nidan verb (lower class) with &apos;hu/fu&apos; ending (archaic)</j:string>
  <j:string key="v2b-s">Nidan verb (lower class) with &apos;bu&apos; ending (archaic)</j:string>
  <j:string key="v2m-s">Nidan verb (lower class) with &apos;mu&apos; ending (archaic)</j:string>
  <j:string key="v2y-s">Nidan verb (lower class) with &apos;yu&apos; ending (archaic)</j:string>
  <j:string key="v2r-s">Nidan verb (lower class) with &apos;ru&apos; ending (archaic)</j:string>
  <j:string key="v2w-s">Nidan verb (lower class) with &apos;u&apos; ending and &apos;we&apos; conjugation (archaic)</j:string>
  <j:string key="archit">architecture term</j:string>
  <j:string key="astron">astronomy, etc. term</j:string>
  <j:string key="baseb">baseball term</j:string>
  <j:string key="biol">biology term</j:string>
  <j:string key="bot">botany term</j:string>
  <j:string key="bus">business term</j:string>
  <j:string key="econ">economics term</j:string>
  <j:string key="engr">engineering term</j:string>
  <j:string key="finc">finance term</j:string>
  <j:string key="geol">geology, etc. term</j:string>
  <j:string key="law">law, etc. term</j:string>
  <j:string key="mahj">mahjong term</j:string>
  <j:string key="med">medicine, etc. term</j:string>
  <j:string key="music">music term</j:string>
  <j:string key="Shinto">Shinto term</j:string>
  <j:string key="shogi">shogi term</j:string>
  <j:string key="sports">sports term</j:string>
  <j:string key="sumo">sumo term</j:string>
  <j:string key="zool">zoology term</j:string>
  <j:string key="joc">jocular, humorous term</j:string>
  <j:string key="anat">anatomical term</j:string>
  <j:string key="Christn">Christian term</j:string>
  <j:string key="net-sl">Internet slang</j:string>
  <j:string key="dated">dated term</j:string>
  <j:string key="hist">historical term</j:string>
  <j:string key="litf">literary or formal term</j:string>
  <j:string key="surname">family or surname</j:string>
  <j:string key="place">place name</j:string>
  <j:string key="unclass">unclassified name</j:string>
  <j:string key="company">company name</j:string>
  <j:string key="product">product name</j:string>
  <j:string key="work">work of art, literature, music, etc. name</j:string>
  <j:string key="person">full name of a particular person</j:string>
  <j:string key="given">given name or forename, gender not specified</j:string>
  <j:string key="station">railway station</j:string>
  <j:string key="organization">organization name</j:string>
</j:map>;

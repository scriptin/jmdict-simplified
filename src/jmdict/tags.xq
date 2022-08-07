xquery version "3.0";
module namespace tags = "tags";

import module namespace j = "http://www.w3.org/2005/xpath-functions";

(: This file is generated, do not edit manually! :)

declare function tags:convert-entity($word-id as xs:string, $text as xs:string) as xs:string? {
  switch($text)
  case "Brazilian" return "bra"
  case "Hokkaido-ben" return "hob"
  case "Kansai-ben" return "ksb"
  case "Kantou-ben" return "ktb"
  case "Kyoto-ben" return "kyb"
  case "Kyuushuu-ben" return "kyu"
  case "Nagano-ben" return "nab"
  case "Osaka-ben" return "osb"
  case "Ryuukyuu-ben" return "rkb"
  case "Touhoku-ben" return "thb"
  case "Tosa-ben" return "tsb"
  case "Tsugaru-ben" return "tsug"
  case "agriculture" return "agric"
  case "anatomy" return "anat"
  case "archeology" return "archeol"
  case "architecture" return "archit"
  case "art, aesthetics" return "art"
  case "astronomy" return "astron"
  case "audiovisual" return "audvid"
  case "aviation" return "aviat"
  case "baseball" return "baseb"
  case "biochemistry" return "biochem"
  case "biology" return "biol"
  case "botany" return "bot"
  case "Buddhism" return "Buddh"
  case "business" return "bus"
  case "card games" return "cards"
  case "chemistry" return "chem"
  case "Christianity" return "Christn"
  case "clothing" return "cloth"
  case "computing" return "comp"
  case "crystallography" return "cryst"
  case "dentistry" return "dent"
  case "ecology" return "ecol"
  case "economics" return "econ"
  case "electricity, elec. eng." return "elec"
  case "electronics" return "electr"
  case "embryology" return "embryo"
  case "engineering" return "engr"
  case "entomology" return "ent"
  case "film" return "film"
  case "finance" return "finc"
  case "fishing" return "fish"
  case "food, cooking" return "food"
  case "gardening, horticulture" return "gardn"
  case "genetics" return "genet"
  case "geography" return "geogr"
  case "geology" return "geol"
  case "geometry" return "geom"
  case "go (game)" return "go"
  case "golf" return "golf"
  case "grammar" return "gramm"
  case "Greek mythology" return "grmyth"
  case "hanafuda" return "hanaf"
  case "horse racing" return "horse"
  case "kabuki" return "kabuki"
  case "law" return "law"
  case "linguistics" return "ling"
  case "logic" return "logic"
  case "martial arts" return "MA"
  case "mahjong" return "mahj"
  case "manga" return "manga"
  case "mathematics" return "math"
  case "mechanical engineering" return "mech"
  case "medicine" return "med"
  case "meteorology" return "met"
  case "military" return "mil"
  case "mining" return "mining"
  case "music" return "music"
  case "noh" return "noh"
  case "ornithology" return "ornith"
  case "paleontology" return "paleo"
  case "pathology" return "pathol"
  case "pharmacology" return "pharm"
  case "philosophy" return "phil"
  case "photography" return "photo"
  case "physics" return "physics"
  case "physiology" return "physiol"
  case "politics" return "politics"
  case "printing" return "print"
  case "psychiatry" return "psy"
  case "psychoanalysis" return "psyanal"
  case "psychology" return "psych"
  case "railway" return "rail"
  case "Roman mythology" return "rommyth"
  case "Shinto" return "Shinto"
  case "shogi" return "shogi"
  case "skiing" return "ski"
  case "sports" return "sports"
  case "statistics" return "stat"
  case "stock market" return "stockm"
  case "sumo" return "sumo"
  case "telecommunications" return "telec"
  case "trademark" return "tradem"
  case "television" return "tv"
  case "video games" return "vidg"
  case "zoology" return "zool"
  case "ateji (phonetic) reading" return "ateji"
  case "word containing irregular kana usage" return "ik"
  case "word containing irregular kanji usage" return "iK"
  case "irregular okurigana usage" return "io"
  case "word containing out-dated kanji or kanji usage" return "oK"
  case "rarely-used kanji form" return "rK"
  case "abbreviation" return "abbr"
  case "archaic" return "arch"
  case "character" return "char"
  case "children's language" return "chn"
  case "colloquial" return "col"
  case "company name" return "company"
  case "creature" return "creat"
  case "dated term" return "dated"
  case "deity" return "dei"
  case "derogatory" return "derog"
  case "document" return "doc"
  case "euphemistic" return "euph"
  case "event" return "ev"
  case "familiar language" return "fam"
  case "female term or language" return "fem"
  case "fiction" return "fict"
  case "formal or literary term" return "form"
  case "given name or forename, gender not specified" return "given"
  case "group" return "group"
  case "historical term" return "hist"
  case "honorific or respectful (sonkeigo) language" return "hon"
  case "humble (kenjougo) language" return "hum"
  case "idiomatic expression" return "id"
  case "jocular, humorous term" return "joc"
  case "legend" return "leg"
  case "manga slang" return "m-sl"
  case "male term or language" return "male"
  case "mythology" return "myth"
  case "Internet slang" return "net-sl"
  case "object" return "obj"
  case "obsolete term" return "obs"
  case "onomatopoeic or mimetic word" return "on-mim"
  case "organization name" return "organization"
  case "other" return "oth"
  case "full name of a particular person" return "person"
  case "place name" return "place"
  case "poetical term" return "poet"
  case "polite (teineigo) language" return "pol"
  case "product name" return "product"
  case "proverb" return "proverb"
  case "quotation" return "quote"
  case "rare term" return "rare"
  case "religion" return "relig"
  case "sensitive" return "sens"
  case "service" return "serv"
  case "ship name" return "ship"
  case "slang" return "sl"
  case "railway station" return "station"
  case "family or surname" return "surname"
  case "word usually written using kana alone" return "uk"
  case "unclassified name" return "unclass"
  case "vulgar expression or word" return "vulg"
  case "work of art, literature, music, etc. name" return "work"
  case "rude or X-rated term (not displayed in educational software)" return "X"
  case "yojijukugo" return "yoji"
  case "noun or verb acting prenominally" return "adj-f"
  case "adjective (keiyoushi)" return "adj-i"
  case "adjective (keiyoushi) - yoi/ii class" return "adj-ix"
  case "'kari' adjective (archaic)" return "adj-kari"
  case "'ku' adjective (archaic)" return "adj-ku"
  case "adjectival nouns or quasi-adjectives (keiyodoshi)" return "adj-na"
  case "archaic/formal form of na-adjective" return "adj-nari"
  case "nouns which may take the genitive case particle 'no'" return "adj-no"
  case "pre-noun adjectival (rentaishi)" return "adj-pn"
  case "'shiku' adjective (archaic)" return "adj-shiku"
  case "'taru' adjective" return "adj-t"
  case "adverb (fukushi)" return "adv"
  case "adverb taking the 'to' particle" return "adv-to"
  case "auxiliary" return "aux"
  case "auxiliary adjective" return "aux-adj"
  case "auxiliary verb" return "aux-v"
  case "conjunction" return "conj"
  case "copula" return "cop"
  case "counter" return "ctr"
  case "expressions (phrases, clauses, etc.)" return "exp"
  case "interjection (kandoushi)" return "int"
  case "noun (common) (futsuumeishi)" return "n"
  case "adverbial noun (fukushitekimeishi)" return "n-adv"
  case "proper noun" return "n-pr"
  case "noun, used as a prefix" return "n-pref"
  case "noun, used as a suffix" return "n-suf"
  case "noun (temporal) (jisoumeishi)" return "n-t"
  case "numeric" return "num"
  case "pronoun" return "pn"
  case "prefix" return "pref"
  case "particle" return "prt"
  case "suffix" return "suf"
  case "unclassified" return "unc"
  case "verb unspecified" return "v-unspec"
  case "Ichidan verb" return "v1"
  case "Ichidan verb - kureru special class" return "v1-s"
  case "Nidan verb with 'u' ending (archaic)" return "v2a-s"
  case "Nidan verb (upper class) with 'bu' ending (archaic)" return "v2b-k"
  case "Nidan verb (lower class) with 'bu' ending (archaic)" return "v2b-s"
  case "Nidan verb (upper class) with 'dzu' ending (archaic)" return "v2d-k"
  case "Nidan verb (lower class) with 'dzu' ending (archaic)" return "v2d-s"
  case "Nidan verb (upper class) with 'gu' ending (archaic)" return "v2g-k"
  case "Nidan verb (lower class) with 'gu' ending (archaic)" return "v2g-s"
  case "Nidan verb (upper class) with 'hu/fu' ending (archaic)" return "v2h-k"
  case "Nidan verb (lower class) with 'hu/fu' ending (archaic)" return "v2h-s"
  case "Nidan verb (upper class) with 'ku' ending (archaic)" return "v2k-k"
  case "Nidan verb (lower class) with 'ku' ending (archaic)" return "v2k-s"
  case "Nidan verb (upper class) with 'mu' ending (archaic)" return "v2m-k"
  case "Nidan verb (lower class) with 'mu' ending (archaic)" return "v2m-s"
  case "Nidan verb (lower class) with 'nu' ending (archaic)" return "v2n-s"
  case "Nidan verb (upper class) with 'ru' ending (archaic)" return "v2r-k"
  case "Nidan verb (lower class) with 'ru' ending (archaic)" return "v2r-s"
  case "Nidan verb (lower class) with 'su' ending (archaic)" return "v2s-s"
  case "Nidan verb (upper class) with 'tsu' ending (archaic)" return "v2t-k"
  case "Nidan verb (lower class) with 'tsu' ending (archaic)" return "v2t-s"
  case "Nidan verb (lower class) with 'u' ending and 'we' conjugation (archaic)" return "v2w-s"
  case "Nidan verb (upper class) with 'yu' ending (archaic)" return "v2y-k"
  case "Nidan verb (lower class) with 'yu' ending (archaic)" return "v2y-s"
  case "Nidan verb (lower class) with 'zu' ending (archaic)" return "v2z-s"
  case "Yodan verb with 'bu' ending (archaic)" return "v4b"
  case "Yodan verb with 'gu' ending (archaic)" return "v4g"
  case "Yodan verb with 'hu/fu' ending (archaic)" return "v4h"
  case "Yodan verb with 'ku' ending (archaic)" return "v4k"
  case "Yodan verb with 'mu' ending (archaic)" return "v4m"
  case "Yodan verb with 'nu' ending (archaic)" return "v4n"
  case "Yodan verb with 'ru' ending (archaic)" return "v4r"
  case "Yodan verb with 'su' ending (archaic)" return "v4s"
  case "Yodan verb with 'tsu' ending (archaic)" return "v4t"
  case "Godan verb - -aru special class" return "v5aru"
  case "Godan verb with 'bu' ending" return "v5b"
  case "Godan verb with 'gu' ending" return "v5g"
  case "Godan verb with 'ku' ending" return "v5k"
  case "Godan verb - Iku/Yuku special class" return "v5k-s"
  case "Godan verb with 'mu' ending" return "v5m"
  case "Godan verb with 'nu' ending" return "v5n"
  case "Godan verb with 'ru' ending" return "v5r"
  case "Godan verb with 'ru' ending (irregular verb)" return "v5r-i"
  case "Godan verb with 'su' ending" return "v5s"
  case "Godan verb with 'tsu' ending" return "v5t"
  case "Godan verb with 'u' ending" return "v5u"
  case "Godan verb with 'u' ending (special class)" return "v5u-s"
  case "Godan verb - Uru old class verb (old form of Eru)" return "v5uru"
  case "intransitive verb" return "vi"
  case "Kuru verb - special class" return "vk"
  case "irregular nu verb" return "vn"
  case "irregular ru verb, plain form ends with -ri" return "vr"
  case "noun or participle which takes the aux. verb suru" return "vs"
  case "su verb - precursor to the modern suru" return "vs-c"
  case "suru verb - included" return "vs-i"
  case "suru verb - special class" return "vs-s"
  case "transitive verb" return "vt"
  case "Ichidan verb - zuru verb (alternative form of -jiru verbs)" return "vz"
  case "gikun (meaning as reading) or jukujikun (special kanji reading)" return "gikun"
  case "out-dated or obsolete kana usage" return "ok"
  case "word usually written using kanji alone" return "uK"
  default return error(
    xs:QName("unknown-tag"),
    concat("Unknown tag '", $text, "' on entity ", $word-id)
  )
};

declare variable $tags:tags := <j:map key="tags">
  <j:string key="bra">Brazilian</j:string>
  <j:string key="hob">Hokkaido-ben</j:string>
  <j:string key="ksb">Kansai-ben</j:string>
  <j:string key="ktb">Kantou-ben</j:string>
  <j:string key="kyb">Kyoto-ben</j:string>
  <j:string key="kyu">Kyuushuu-ben</j:string>
  <j:string key="nab">Nagano-ben</j:string>
  <j:string key="osb">Osaka-ben</j:string>
  <j:string key="rkb">Ryuukyuu-ben</j:string>
  <j:string key="thb">Touhoku-ben</j:string>
  <j:string key="tsb">Tosa-ben</j:string>
  <j:string key="tsug">Tsugaru-ben</j:string>
  <j:string key="agric">agriculture</j:string>
  <j:string key="anat">anatomy</j:string>
  <j:string key="archeol">archeology</j:string>
  <j:string key="archit">architecture</j:string>
  <j:string key="art">art, aesthetics</j:string>
  <j:string key="astron">astronomy</j:string>
  <j:string key="audvid">audiovisual</j:string>
  <j:string key="aviat">aviation</j:string>
  <j:string key="baseb">baseball</j:string>
  <j:string key="biochem">biochemistry</j:string>
  <j:string key="biol">biology</j:string>
  <j:string key="bot">botany</j:string>
  <j:string key="Buddh">Buddhism</j:string>
  <j:string key="bus">business</j:string>
  <j:string key="cards">card games</j:string>
  <j:string key="chem">chemistry</j:string>
  <j:string key="Christn">Christianity</j:string>
  <j:string key="cloth">clothing</j:string>
  <j:string key="comp">computing</j:string>
  <j:string key="cryst">crystallography</j:string>
  <j:string key="dent">dentistry</j:string>
  <j:string key="ecol">ecology</j:string>
  <j:string key="econ">economics</j:string>
  <j:string key="elec">electricity, elec. eng.</j:string>
  <j:string key="electr">electronics</j:string>
  <j:string key="embryo">embryology</j:string>
  <j:string key="engr">engineering</j:string>
  <j:string key="ent">entomology</j:string>
  <j:string key="film">film</j:string>
  <j:string key="finc">finance</j:string>
  <j:string key="fish">fishing</j:string>
  <j:string key="food">food, cooking</j:string>
  <j:string key="gardn">gardening, horticulture</j:string>
  <j:string key="genet">genetics</j:string>
  <j:string key="geogr">geography</j:string>
  <j:string key="geol">geology</j:string>
  <j:string key="geom">geometry</j:string>
  <j:string key="go">go (game)</j:string>
  <j:string key="golf">golf</j:string>
  <j:string key="gramm">grammar</j:string>
  <j:string key="grmyth">Greek mythology</j:string>
  <j:string key="hanaf">hanafuda</j:string>
  <j:string key="horse">horse racing</j:string>
  <j:string key="kabuki">kabuki</j:string>
  <j:string key="law">law</j:string>
  <j:string key="ling">linguistics</j:string>
  <j:string key="logic">logic</j:string>
  <j:string key="MA">martial arts</j:string>
  <j:string key="mahj">mahjong</j:string>
  <j:string key="manga">manga</j:string>
  <j:string key="math">mathematics</j:string>
  <j:string key="mech">mechanical engineering</j:string>
  <j:string key="med">medicine</j:string>
  <j:string key="met">meteorology</j:string>
  <j:string key="mil">military</j:string>
  <j:string key="mining">mining</j:string>
  <j:string key="music">music</j:string>
  <j:string key="noh">noh</j:string>
  <j:string key="ornith">ornithology</j:string>
  <j:string key="paleo">paleontology</j:string>
  <j:string key="pathol">pathology</j:string>
  <j:string key="pharm">pharmacology</j:string>
  <j:string key="phil">philosophy</j:string>
  <j:string key="photo">photography</j:string>
  <j:string key="physics">physics</j:string>
  <j:string key="physiol">physiology</j:string>
  <j:string key="politics">politics</j:string>
  <j:string key="print">printing</j:string>
  <j:string key="psy">psychiatry</j:string>
  <j:string key="psyanal">psychoanalysis</j:string>
  <j:string key="psych">psychology</j:string>
  <j:string key="rail">railway</j:string>
  <j:string key="rommyth">Roman mythology</j:string>
  <j:string key="Shinto">Shinto</j:string>
  <j:string key="shogi">shogi</j:string>
  <j:string key="ski">skiing</j:string>
  <j:string key="sports">sports</j:string>
  <j:string key="stat">statistics</j:string>
  <j:string key="stockm">stock market</j:string>
  <j:string key="sumo">sumo</j:string>
  <j:string key="telec">telecommunications</j:string>
  <j:string key="tradem">trademark</j:string>
  <j:string key="tv">television</j:string>
  <j:string key="vidg">video games</j:string>
  <j:string key="zool">zoology</j:string>
  <j:string key="ateji">ateji (phonetic) reading</j:string>
  <j:string key="ik">word containing irregular kana usage</j:string>
  <j:string key="iK">word containing irregular kanji usage</j:string>
  <j:string key="io">irregular okurigana usage</j:string>
  <j:string key="oK">word containing out-dated kanji or kanji usage</j:string>
  <j:string key="rK">rarely-used kanji form</j:string>
  <j:string key="abbr">abbreviation</j:string>
  <j:string key="arch">archaic</j:string>
  <j:string key="char">character</j:string>
  <j:string key="chn">children&apos;s language</j:string>
  <j:string key="col">colloquial</j:string>
  <j:string key="company">company name</j:string>
  <j:string key="creat">creature</j:string>
  <j:string key="dated">dated term</j:string>
  <j:string key="dei">deity</j:string>
  <j:string key="derog">derogatory</j:string>
  <j:string key="doc">document</j:string>
  <j:string key="euph">euphemistic</j:string>
  <j:string key="ev">event</j:string>
  <j:string key="fam">familiar language</j:string>
  <j:string key="fem">female term or language</j:string>
  <j:string key="fict">fiction</j:string>
  <j:string key="form">formal or literary term</j:string>
  <j:string key="given">given name or forename, gender not specified</j:string>
  <j:string key="group">group</j:string>
  <j:string key="hist">historical term</j:string>
  <j:string key="hon">honorific or respectful (sonkeigo) language</j:string>
  <j:string key="hum">humble (kenjougo) language</j:string>
  <j:string key="id">idiomatic expression</j:string>
  <j:string key="joc">jocular, humorous term</j:string>
  <j:string key="leg">legend</j:string>
  <j:string key="m-sl">manga slang</j:string>
  <j:string key="male">male term or language</j:string>
  <j:string key="myth">mythology</j:string>
  <j:string key="net-sl">Internet slang</j:string>
  <j:string key="obj">object</j:string>
  <j:string key="obs">obsolete term</j:string>
  <j:string key="on-mim">onomatopoeic or mimetic word</j:string>
  <j:string key="organization">organization name</j:string>
  <j:string key="oth">other</j:string>
  <j:string key="person">full name of a particular person</j:string>
  <j:string key="place">place name</j:string>
  <j:string key="poet">poetical term</j:string>
  <j:string key="pol">polite (teineigo) language</j:string>
  <j:string key="product">product name</j:string>
  <j:string key="proverb">proverb</j:string>
  <j:string key="quote">quotation</j:string>
  <j:string key="rare">rare term</j:string>
  <j:string key="relig">religion</j:string>
  <j:string key="sens">sensitive</j:string>
  <j:string key="serv">service</j:string>
  <j:string key="ship">ship name</j:string>
  <j:string key="sl">slang</j:string>
  <j:string key="station">railway station</j:string>
  <j:string key="surname">family or surname</j:string>
  <j:string key="uk">word usually written using kana alone</j:string>
  <j:string key="unclass">unclassified name</j:string>
  <j:string key="vulg">vulgar expression or word</j:string>
  <j:string key="work">work of art, literature, music, etc. name</j:string>
  <j:string key="X">rude or X-rated term (not displayed in educational software)</j:string>
  <j:string key="yoji">yojijukugo</j:string>
  <j:string key="adj-f">noun or verb acting prenominally</j:string>
  <j:string key="adj-i">adjective (keiyoushi)</j:string>
  <j:string key="adj-ix">adjective (keiyoushi) - yoi/ii class</j:string>
  <j:string key="adj-kari">&apos;kari&apos; adjective (archaic)</j:string>
  <j:string key="adj-ku">&apos;ku&apos; adjective (archaic)</j:string>
  <j:string key="adj-na">adjectival nouns or quasi-adjectives (keiyodoshi)</j:string>
  <j:string key="adj-nari">archaic/formal form of na-adjective</j:string>
  <j:string key="adj-no">nouns which may take the genitive case particle &apos;no&apos;</j:string>
  <j:string key="adj-pn">pre-noun adjectival (rentaishi)</j:string>
  <j:string key="adj-shiku">&apos;shiku&apos; adjective (archaic)</j:string>
  <j:string key="adj-t">&apos;taru&apos; adjective</j:string>
  <j:string key="adv">adverb (fukushi)</j:string>
  <j:string key="adv-to">adverb taking the &apos;to&apos; particle</j:string>
  <j:string key="aux">auxiliary</j:string>
  <j:string key="aux-adj">auxiliary adjective</j:string>
  <j:string key="aux-v">auxiliary verb</j:string>
  <j:string key="conj">conjunction</j:string>
  <j:string key="cop">copula</j:string>
  <j:string key="ctr">counter</j:string>
  <j:string key="exp">expressions (phrases, clauses, etc.)</j:string>
  <j:string key="int">interjection (kandoushi)</j:string>
  <j:string key="n">noun (common) (futsuumeishi)</j:string>
  <j:string key="n-adv">adverbial noun (fukushitekimeishi)</j:string>
  <j:string key="n-pr">proper noun</j:string>
  <j:string key="n-pref">noun, used as a prefix</j:string>
  <j:string key="n-suf">noun, used as a suffix</j:string>
  <j:string key="n-t">noun (temporal) (jisoumeishi)</j:string>
  <j:string key="num">numeric</j:string>
  <j:string key="pn">pronoun</j:string>
  <j:string key="pref">prefix</j:string>
  <j:string key="prt">particle</j:string>
  <j:string key="suf">suffix</j:string>
  <j:string key="unc">unclassified</j:string>
  <j:string key="v-unspec">verb unspecified</j:string>
  <j:string key="v1">Ichidan verb</j:string>
  <j:string key="v1-s">Ichidan verb - kureru special class</j:string>
  <j:string key="v2a-s">Nidan verb with &apos;u&apos; ending (archaic)</j:string>
  <j:string key="v2b-k">Nidan verb (upper class) with &apos;bu&apos; ending (archaic)</j:string>
  <j:string key="v2b-s">Nidan verb (lower class) with &apos;bu&apos; ending (archaic)</j:string>
  <j:string key="v2d-k">Nidan verb (upper class) with &apos;dzu&apos; ending (archaic)</j:string>
  <j:string key="v2d-s">Nidan verb (lower class) with &apos;dzu&apos; ending (archaic)</j:string>
  <j:string key="v2g-k">Nidan verb (upper class) with &apos;gu&apos; ending (archaic)</j:string>
  <j:string key="v2g-s">Nidan verb (lower class) with &apos;gu&apos; ending (archaic)</j:string>
  <j:string key="v2h-k">Nidan verb (upper class) with &apos;hu/fu&apos; ending (archaic)</j:string>
  <j:string key="v2h-s">Nidan verb (lower class) with &apos;hu/fu&apos; ending (archaic)</j:string>
  <j:string key="v2k-k">Nidan verb (upper class) with &apos;ku&apos; ending (archaic)</j:string>
  <j:string key="v2k-s">Nidan verb (lower class) with &apos;ku&apos; ending (archaic)</j:string>
  <j:string key="v2m-k">Nidan verb (upper class) with &apos;mu&apos; ending (archaic)</j:string>
  <j:string key="v2m-s">Nidan verb (lower class) with &apos;mu&apos; ending (archaic)</j:string>
  <j:string key="v2n-s">Nidan verb (lower class) with &apos;nu&apos; ending (archaic)</j:string>
  <j:string key="v2r-k">Nidan verb (upper class) with &apos;ru&apos; ending (archaic)</j:string>
  <j:string key="v2r-s">Nidan verb (lower class) with &apos;ru&apos; ending (archaic)</j:string>
  <j:string key="v2s-s">Nidan verb (lower class) with &apos;su&apos; ending (archaic)</j:string>
  <j:string key="v2t-k">Nidan verb (upper class) with &apos;tsu&apos; ending (archaic)</j:string>
  <j:string key="v2t-s">Nidan verb (lower class) with &apos;tsu&apos; ending (archaic)</j:string>
  <j:string key="v2w-s">Nidan verb (lower class) with &apos;u&apos; ending and &apos;we&apos; conjugation (archaic)</j:string>
  <j:string key="v2y-k">Nidan verb (upper class) with &apos;yu&apos; ending (archaic)</j:string>
  <j:string key="v2y-s">Nidan verb (lower class) with &apos;yu&apos; ending (archaic)</j:string>
  <j:string key="v2z-s">Nidan verb (lower class) with &apos;zu&apos; ending (archaic)</j:string>
  <j:string key="v4b">Yodan verb with &apos;bu&apos; ending (archaic)</j:string>
  <j:string key="v4g">Yodan verb with &apos;gu&apos; ending (archaic)</j:string>
  <j:string key="v4h">Yodan verb with &apos;hu/fu&apos; ending (archaic)</j:string>
  <j:string key="v4k">Yodan verb with &apos;ku&apos; ending (archaic)</j:string>
  <j:string key="v4m">Yodan verb with &apos;mu&apos; ending (archaic)</j:string>
  <j:string key="v4n">Yodan verb with &apos;nu&apos; ending (archaic)</j:string>
  <j:string key="v4r">Yodan verb with &apos;ru&apos; ending (archaic)</j:string>
  <j:string key="v4s">Yodan verb with &apos;su&apos; ending (archaic)</j:string>
  <j:string key="v4t">Yodan verb with &apos;tsu&apos; ending (archaic)</j:string>
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
  <j:string key="vi">intransitive verb</j:string>
  <j:string key="vk">Kuru verb - special class</j:string>
  <j:string key="vn">irregular nu verb</j:string>
  <j:string key="vr">irregular ru verb, plain form ends with -ri</j:string>
  <j:string key="vs">noun or participle which takes the aux. verb suru</j:string>
  <j:string key="vs-c">su verb - precursor to the modern suru</j:string>
  <j:string key="vs-i">suru verb - included</j:string>
  <j:string key="vs-s">suru verb - special class</j:string>
  <j:string key="vt">transitive verb</j:string>
  <j:string key="vz">Ichidan verb - zuru verb (alternative form of -jiru verbs)</j:string>
  <j:string key="gikun">gikun (meaning as reading) or jukujikun (special kanji reading)</j:string>
  <j:string key="ok">out-dated or obsolete kana usage</j:string>
  <j:string key="uK">word usually written using kanji alone</j:string>
</j:map>;

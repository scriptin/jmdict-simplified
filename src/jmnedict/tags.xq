xquery version "3.0";
module namespace tags = "tags";

import module namespace j = "http://www.w3.org/2005/xpath-functions";

(: This file is generated, do not edit manually! :)

declare function tags:convert-entity($word-id as xs:string, $text as xs:string) as xs:string? {
  switch($text)
  case "character" return "char"
  case "company name" return "company"
  case "creature" return "creat"
  case "deity" return "dei"
  case "document" return "doc"
  case "event" return "ev"
  case "female given name or forename" return "fem"
  case "fiction" return "fict"
  case "given name or forename, gender not specified" return "given"
  case "group" return "group"
  case "legend" return "leg"
  case "male given name or forename" return "masc"
  case "mythology" return "myth"
  case "object" return "obj"
  case "organization name" return "organization"
  case "other" return "oth"
  case "full name of a particular person" return "person"
  case "place name" return "place"
  case "product name" return "product"
  case "religion" return "relig"
  case "service" return "serv"
  case "ship name" return "ship"
  case "railway station" return "station"
  case "family or surname" return "surname"
  case "unclassified name" return "unclass"
  case "work of art, literature, music, etc. name" return "work"
  default return error(
    xs:QName("unknown-tag"),
    concat("Unknown tag '", $text, "' on entity ", $word-id)
  )
};

declare variable $tags:tags := <j:map key="tags">
  <j:string key="char">character</j:string>
  <j:string key="company">company name</j:string>
  <j:string key="creat">creature</j:string>
  <j:string key="dei">deity</j:string>
  <j:string key="doc">document</j:string>
  <j:string key="ev">event</j:string>
  <j:string key="fem">female given name or forename</j:string>
  <j:string key="fict">fiction</j:string>
  <j:string key="given">given name or forename, gender not specified</j:string>
  <j:string key="group">group</j:string>
  <j:string key="leg">legend</j:string>
  <j:string key="masc">male given name or forename</j:string>
  <j:string key="myth">mythology</j:string>
  <j:string key="obj">object</j:string>
  <j:string key="organization">organization name</j:string>
  <j:string key="oth">other</j:string>
  <j:string key="person">full name of a particular person</j:string>
  <j:string key="place">place name</j:string>
  <j:string key="product">product name</j:string>
  <j:string key="relig">religion</j:string>
  <j:string key="serv">service</j:string>
  <j:string key="ship">ship name</j:string>
  <j:string key="station">railway station</j:string>
  <j:string key="surname">family or surname</j:string>
  <j:string key="unclass">unclassified name</j:string>
  <j:string key="work">work of art, literature, music, etc. name</j:string>
</j:map>;

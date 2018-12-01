xquery version "3.0";
module namespace tags = "tags";

import module namespace j = "http://www.w3.org/2005/xpath-functions";

(: This file is generated, do not edit manually! :)

declare function tags:convert-entity($word-id as xs:string, $text as xs:string) as xs:string? {
  switch($text)
  case "family or surname" return "surname"
  case "place name" return "place"
  case "unclassified name" return "unclass"
  case "company name" return "company"
  case "product name" return "product"
  case "work of art, literature, music, etc. name" return "work"
  case "male given name or forename" return "masc"
  case "female given name or forename" return "fem"
  case "full name of a particular person" return "person"
  case "given name or forename, gender not specified" return "given"
  case "railway station" return "station"
  case "organization name" return "organization"
  case "old or irregular kana form" return "ok"
  default return error(
    xs:QName("unknown-tag"),
    concat("Unknown tag '", $text, "' on entity ", $word-id)
  )
};

declare variable $tags:tags := <j:map key="tags">
  <j:string key="surname">family or surname</j:string>
  <j:string key="place">place name</j:string>
  <j:string key="unclass">unclassified name</j:string>
  <j:string key="company">company name</j:string>
  <j:string key="product">product name</j:string>
  <j:string key="work">work of art, literature, music, etc. name</j:string>
  <j:string key="masc">male given name or forename</j:string>
  <j:string key="fem">female given name or forename</j:string>
  <j:string key="person">full name of a particular person</j:string>
  <j:string key="given">given name or forename, gender not specified</j:string>
  <j:string key="station">railway station</j:string>
  <j:string key="organization">organization name</j:string>
  <j:string key="ok">old or irregular kana form</j:string>
</j:map>;

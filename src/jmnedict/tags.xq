xquery version "3.0";
module namespace tags = "tags";

import module namespace tags-utils = "tags-utils" at "../tags-utils.xq";

(: This file is generated, do not edit manually! :)

declare function tags:convert-entity($word-id as xs:string, $text as xs:string) as xs:string? {
  tags:convert($word-id, tags-utils:deduplicate(normalize-space($text)))
};

declare function tags:convert($word-id as xs:string, $text as xs:string) as xs:string? {
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

declare variable $tags:tags := <pair name="tags" type="object">
  <pair name="surname" type="string">family or surname</pair>
  <pair name="place" type="string">place name</pair>
  <pair name="unclass" type="string">unclassified name</pair>
  <pair name="company" type="string">company name</pair>
  <pair name="product" type="string">product name</pair>
  <pair name="work" type="string">work of art, literature, music, etc. name</pair>
  <pair name="masc" type="string">male given name or forename</pair>
  <pair name="fem" type="string">female given name or forename</pair>
  <pair name="person" type="string">full name of a particular person</pair>
  <pair name="given" type="string">given name or forename, gender not specified</pair>
  <pair name="station" type="string">railway station</pair>
  <pair name="organization" type="string">organization name</pair>
  <pair name="ok" type="string">old or irregular kana form</pair>
</pair>;

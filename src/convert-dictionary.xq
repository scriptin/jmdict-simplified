xquery version "3.0";

import module namespace jx = "http://zorba.io/modules/json-xml";
import module namespace transform = "transform" at "transform.xq";
import module namespace tags = "tags" at "tags.xq";

declare variable $doc external;

jx:xml-to-json(
  <json type="object">
    { transform:extract-date($doc) }
    { transform:extract-revisions($doc) }
    { $tags:tags }
    <pair name="words" type="array">
      { for $word in $doc/JMdict/entry
        return transform:word($word) }
    </pair>
  </json>
)

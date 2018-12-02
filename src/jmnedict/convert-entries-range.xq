xquery version "3.0";

import module namespace j = "http://www.w3.org/2005/xpath-functions";
import module namespace transform = "transform" at "transform.xq";
import module namespace tags = "tags" at "tags.xq";

declare variable $version external;
declare variable $start as xs:int external;
declare variable $end as xs:int external;

fn:xml-to-json(
  <j:map>
    <j:array key="words">
      { for $word in /JMnedict/entry[position() >= $start and position() < $end]
        return transform:word($word) }
    </j:array>
  </j:map>
)

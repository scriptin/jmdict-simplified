xquery version "3.0";

import module namespace j = "http://www.w3.org/2005/xpath-functions";
import module namespace transform = "transform" at "transform.xq";
import module namespace tags = "tags" at "tags.xq";

declare variable $version external;

fn:xml-to-json(
  <j:map>
    <j:string key="version"> { $version } </j:string>
    { transform:extract-date(/) }
    { transform:extract-revisions(/) }
    { $tags:tags }
    <j:array key="words">
      { for $word in /JMdict/entry
        return transform:word($word) }
    </j:array>
  </j:map>
)

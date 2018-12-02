xquery version "3.0";
module namespace util = "util";

import module namespace j = "http://www.w3.org/2005/xpath-functions";
import module namespace tags = "tags" at "tags.xq";

(:
  Extract a creation date from a comment with following format: "JM(ne)dict created: YYYY-MM-DD".
:)
declare function util:extract-date($doc as node()) as node() {
  let $version-comment := string($doc//comment()[contains(., 'dict created')][1])
  return <j:string key="dictDate">
    { normalize-space(substring-after($version-comment, ':')) }
  </j:string>
};

(:
  Extract revision numbers, as they appear in comments before DOCTYPE.
  Strictly speaking, these are version numbers of a dictionary, but they are not mentioned in official documentation.
:)
declare function util:extract-revisions($doc as node()) as node() {
  <j:array key="dictRevisions">
    { for $rev in $doc//comment()[matches(., 'Rev \d+\.\d+')]
      let $first-line := substring-before(string($rev), '&#10;')
      return <j:string> { normalize-space(substring-after($first-line, 'Rev')) } </j:string> }
  </j:array>
};

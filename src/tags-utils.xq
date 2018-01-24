xquery version "3.0";
module namespace tags-utils = "tags-utils";

declare function tags-utils:deduplicate($text as xs:string) as xs:string? {
  let $len := string-length($text) div 2
  let $fst := substring($text, 1, $len)
  let $snd := substring($text, $len+1)
  return if ($fst = $snd) then $fst else $text
};

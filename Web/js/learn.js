  <script type="text/javascript">
  var ss = new Array();
  ss[0] = "Joe's Cooking Book";
  ss[1] = "Sam's Cookbook";
  ss[2] = "JavaScript CookBook";
  ss[3]  = "JavaScript cookbook";
  var pattern = /Cook.*Book/;
  for (var i = 0; i < ss.length; i++) 
  alert(ss[i] + " " + pattern.test(ss[i]));
  </script>

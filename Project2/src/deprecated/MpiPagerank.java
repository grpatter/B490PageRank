
    

  

<!DOCTYPE html>
<html>
  <head>
    <meta charset='utf-8'>
    <meta http-equiv="X-UA-Compatible" content="chrome=1">
        <title>Project2/src/deprecated/MpiPagerank.java at c97f6bf2fb2f74222c2d46593c440cd32124d1a7 from grpatter/B490PageRank - GitHub</title>
    <link rel="search" type="application/opensearchdescription+xml" href="/opensearch.xml" title="GitHub" />
    <link rel="fluid-icon" href="https://github.com/fluidicon.png" title="GitHub" />

    <link href="https://d3nwyuy0nl342s.cloudfront.net/3685cd012847cd263a7c01aa5098c138bbd0d0f8/stylesheets/bundle_common.css" media="screen" rel="stylesheet" type="text/css" />
<link href="https://d3nwyuy0nl342s.cloudfront.net/3685cd012847cd263a7c01aa5098c138bbd0d0f8/stylesheets/bundle_github.css" media="screen" rel="stylesheet" type="text/css" />
    

    <script type="text/javascript">
      if (typeof console == "undefined" || typeof console.log == "undefined")
        console = { log: function() {} }
    </script>
    <script type="text/javascript" charset="utf-8">
      var GitHub = {
        assetHost: 'https://d3nwyuy0nl342s.cloudfront.net'
      }
      var github_user = 'grpatter'
      
    </script>
    <script src="https://d3nwyuy0nl342s.cloudfront.net/3685cd012847cd263a7c01aa5098c138bbd0d0f8/javascripts/jquery/jquery-1.4.2.min.js" type="text/javascript"></script>
    <script src="https://d3nwyuy0nl342s.cloudfront.net/3685cd012847cd263a7c01aa5098c138bbd0d0f8/javascripts/bundle_common.js" type="text/javascript"></script>
<script src="https://d3nwyuy0nl342s.cloudfront.net/3685cd012847cd263a7c01aa5098c138bbd0d0f8/javascripts/bundle_github.js" type="text/javascript"></script>


    
    <script type="text/javascript" charset="utf-8">
      GitHub.spy({
        repo: "grpatter/B490PageRank"
      })
    </script>

    
  <link href="https://github.com/grpatter/B490PageRank/commits/c97f6bf2fb2f74222c2d46593c440cd32124d1a7.atom" rel="alternate" title="Recent Commits to B490PageRank:c97f6bf2fb2f74222c2d46593c440cd32124d1a7" type="application/atom+xml" />

    <META NAME="ROBOTS" CONTENT="NOINDEX, FOLLOW">    <meta name="description" content="B490 PageRank Impl" />
    <script type="text/javascript">
      GitHub.nameWithOwner = GitHub.nameWithOwner || "grpatter/B490PageRank";
      GitHub.currentRef = '';
      GitHub.commitSHA = "c97f6bf2fb2f74222c2d46593c440cd32124d1a7";
      GitHub.currentPath = 'Project2/src/deprecated/MpiPagerank.java';
      GitHub.masterBranch = "master";

      
    </script>
  

        <script type="text/javascript">
      var _gaq = _gaq || [];
      _gaq.push(['_setAccount', 'UA-3769691-2']);
      _gaq.push(['_setDomainName', 'none']);
      _gaq.push(['_trackPageview']);
      (function() {
        var ga = document.createElement('script');
        ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
        ga.setAttribute('async', 'true');
        document.documentElement.firstChild.appendChild(ga);
      })();
    </script>

    
  </head>

  

  <body class="logged_in page-blob">
    

    

    

    

    

    <div class="subnavd" id="main">
      <div id="header" class="true">
        
          <a class="logo boring" href="https://github.com/">
            <img alt="github" class="default" src="https://d3nwyuy0nl342s.cloudfront.net/images/modules/header/logov3.png" />
            <!--[if (gt IE 8)|!(IE)]><!-->
            <img alt="github" class="hover" src="https://d3nwyuy0nl342s.cloudfront.net/images/modules/header/logov3-hover.png" />
            <!--<![endif]-->
          </a>
        
        
          





  
    <div class="userbox">
      <div class="avatarname">
        <a href="https://github.com/grpatter"><img src="https://secure.gravatar.com/avatar/af8dcbcc76b84570ffc999f4f514fdff?s=140&d=https://d3nwyuy0nl342s.cloudfront.net%2Fimages%2Fgravatars%2Fgravatar-140.png" alt="" width="20" height="20"  /></a>
        <a href="https://github.com/grpatter" class="name">grpatter</a>

        
        
          <a href="https://github.com/inbox/notifications" class="unread_count notifications_count new tooltipped downwards js-notification-count" title="Unread Notifications">181</a>
        
      </div>
      <ul class="usernav">
        <li><a href="https://github.com/">Dashboard</a></li>
        <li>
          
          <a href="https://github.com/inbox">Inbox</a>
          <a href="https://github.com/inbox" class="unread_count ">0</a>
        </li>
        <li><a href="https://github.com/account">Account Settings</a></li>
                <li><a href="/logout">Log Out</a></li>
      </ul>
    </div><!-- /.userbox -->
  


        
        <div class="topsearch">
  
    <form action="/search" id="top_search_form" method="get">
      <a href="/search" class="advanced-search tooltipped downwards" title="Advanced Search">Advanced Search</a>
      <input type="search" class="search my_repos_autocompleter" name="q" results="5" placeholder="Search&hellip;" /> <input type="submit" value="Search" class="button" />
      <input type="hidden" name="type" value="Everything" />
      <input type="hidden" name="repo" value="" />
      <input type="hidden" name="langOverride" value="" />
      <input type="hidden" name="start_value" value="1" />
    </form>
    <ul class="nav">
      <li><a href="/explore">Explore GitHub</a></li>
      <li><a href="https://gist.github.com">Gist</a></li>
      <li><a href="/blog">Blog</a></li>
      <li><a href="http://help.github.com">Help</a></li>
    </ul>
  
</div>

      </div>

      
      
        
    <div class="site">
      <div class="pagehead repohead vis-public    instapaper_ignore readability-menu">

      

      <div class="title-actions-bar">
        <h1>
          <a href="/grpatter">grpatter</a> / <strong><a href="https://github.com/grpatter/B490PageRank">B490PageRank</a></strong>
          
          
        </h1>

        
    <ul class="actions">
      

      
        <li class="for-owner" style="display:none"><a href="https://github.com/grpatter/B490PageRank/admin" class="minibutton btn-admin "><span><span class="icon"></span>Admin</span></a></li>
        <li>
          <a href="/grpatter/B490PageRank/toggle_watch" class="minibutton btn-watch " id="watch_button" onclick="var f = document.createElement('form'); f.style.display = 'none'; this.parentNode.appendChild(f); f.method = 'POST'; f.action = this.href;var s = document.createElement('input'); s.setAttribute('type', 'hidden'); s.setAttribute('name', 'authenticity_token'); s.setAttribute('value', '80be9f430bb26fc4b7bf36ff77aaada31998d567'); f.appendChild(s);f.submit();return false;" style="display:none"><span><span class="icon"></span>Watch</span></a>
          <a href="/grpatter/B490PageRank/toggle_watch" class="minibutton btn-watch " id="unwatch_button" onclick="var f = document.createElement('form'); f.style.display = 'none'; this.parentNode.appendChild(f); f.method = 'POST'; f.action = this.href;var s = document.createElement('input'); s.setAttribute('type', 'hidden'); s.setAttribute('name', 'authenticity_token'); s.setAttribute('value', '80be9f430bb26fc4b7bf36ff77aaada31998d567'); f.appendChild(s);f.submit();return false;" style="display:none"><span><span class="icon"></span>Unwatch</span></a>
        </li>
        
          
            <li class="for-notforked" style="display:none"><a href="/grpatter/B490PageRank/fork" class="minibutton btn-fork " id="fork_button" onclick="var f = document.createElement('form'); f.style.display = 'none'; this.parentNode.appendChild(f); f.method = 'POST'; f.action = this.href;var s = document.createElement('input'); s.setAttribute('type', 'hidden'); s.setAttribute('name', 'authenticity_token'); s.setAttribute('value', '80be9f430bb26fc4b7bf36ff77aaada31998d567'); f.appendChild(s);f.submit();return false;"><span><span class="icon"></span>Fork</span></a></li>
            <li class="for-hasfork" style="display:none"><a href="#" class="minibutton btn-fork " id="your_fork_button"><span><span class="icon"></span>Your Fork</span></a></li>
          

          <li id='pull_request_item' class='nspr' style='display:none'><a href="/grpatter/B490PageRank/pull/new/c97f6bf2fb2f74222c2d46593c440cd32124d1a7" class="minibutton btn-pull-request "><span><span class="icon"></span>Pull Request</span></a></li>
        
      
      
      <li class="repostats">
        <ul class="repo-stats">
          <li class="watchers"><a href="/grpatter/B490PageRank/watchers" title="Watchers" class="tooltipped downwards">4</a></li>
          <li class="forks"><a href="/grpatter/B490PageRank/network" title="Forks" class="tooltipped downwards">1</a></li>
        </ul>
      </li>
    </ul>

      </div>

        
  <ul class="tabs">
    <li><a href="https://github.com/grpatter/B490PageRank/tree/" class="selected" highlight="repo_source">Source</a></li>
    <li><a href="https://github.com/grpatter/B490PageRank/commits/" highlight="repo_commits">Commits</a></li>
    <li><a href="/grpatter/B490PageRank/network" highlight="repo_network">Network</a></li>
    <li><a href="/grpatter/B490PageRank/pulls" highlight="repo_pulls">Pull Requests (0)</a></li>

    
      <li><a href="/grpatter/B490PageRank/forkqueue" highlight="repo_fork_queue">Fork Queue</a></li>
    

    
      
      <li><a href="/grpatter/B490PageRank/issues" highlight="issues">Issues (0)</a></li>
    

                <li><a href="/grpatter/B490PageRank/wiki" highlight="repo_wiki">Wiki (2)</a></li>
        
    <li><a href="/grpatter/B490PageRank/graphs" highlight="repo_graphs">Graphs</a></li>

    <li class="contextswitch nochoices">
      <span class="toggle leftwards tooltipped" title="c97f6bf2fb2f74222c2d46593c440cd32124d1a7">
        <em>Tree:</em>
        <code>c97f6bf</code>
      </span>
    </li>
  </ul>

  <div style="display:none" id="pl-description"><p><em class="placeholder">click here to add a description</em></p></div>
  <div style="display:none" id="pl-homepage"><p><em class="placeholder">click here to add a homepage</em></p></div>

  <div class="subnav-bar">
  
  <ul>
    <li>
      <a href="#" class="dropdown">Switch Branches (3)</a>
      <ul>
        
          
          
            <li><a href="/grpatter/B490PageRank/blob/gh-pages/Project2/src/deprecated/MpiPagerank.java" action="show">gh-pages</a></li>
          
        
          
          
            <li><a href="/grpatter/B490PageRank/blob/jonstout/Project2/src/deprecated/MpiPagerank.java" action="show">jonstout</a></li>
          
        
          
          
            <li><a href="/grpatter/B490PageRank/blob/master/Project2/src/deprecated/MpiPagerank.java" action="show">master</a></li>
          
        
      </ul>
    </li>
    <li>
      <a href="#" class="dropdown defunct">Switch Tags (0)</a>
      
    </li>
    <li>
    
    <a href="/grpatter/B490PageRank/branches/master" class="manage">Branch List</a>
    
    </li>
  </ul>
</div>

  
  
  
  
  
  



        
    <div class="frame frame-center tree-finder" style="display: none">
      <div class="breadcrumb">
        <b><a href="/grpatter/B490PageRank">B490PageRank</a></b> /
        <input class="tree-finder-input" type="text" name="query" autocomplete="off" spellcheck="false">
      </div>

      
        <div class="octotip">
          <p>
            <a href="/grpatter/B490PageRank/dismiss-tree-finder-help" class="dismiss js-dismiss-tree-list-help" title="Hide this notice forever">Dismiss</a>
            <strong>Octotip:</strong> You've activated the <em>file finder</em> by pressing <span class="kbd">t</span>
            Start typing to filter the file list. Use <span class="kbd badmono">↑</span> and <span class="kbd badmono">↓</span> to navigate,
            <span class="kbd">enter</span> to view files.
          </p>
        </div>
      

      <table class="tree-browser" cellpadding="0" cellspacing="0">
        <tr class="js-header"><th>&nbsp;</th><th>name</th></tr>
        <tr class="js-no-results no-results" style="display: none">
          <th colspan="2">No matching files</th>
        </tr>
        <tbody class="js-results-list">
        </tbody>
      </table>
    </div>

    <div id="jump-to-line" style="display:none">
      <h2>Jump to Line</h2>
      <form>
        <input class="textfield" type="text">
        <div class="full-button">
          <button type="submit" class="classy">
            <span>Go</span>
          </button>
        </div>
      </form>
    </div>

    <div id="repo_details" class="metabox clearfix">
      <div id="repo_details_loader" class="metabox-loader" style="display:none">Sending Request&hellip;</div>

        <a href="/grpatter/B490PageRank/downloads" class="download-source" id="download_button" title="Download source, tagged packages and binaries."><span class="icon"></span>Downloads</a>

      <div id="repository_desc_wrapper">
      <div id="repository_description" rel="repository_description_edit">
        
          <p>B490 PageRank Impl
            <span id="read_more" style="display:none">&mdash; <a href="#readme">Read more</a></span>
          </p>
        
      </div>

      <div id="repository_description_edit" style="display:none;" class="inline-edit">
        <form action="/grpatter/B490PageRank/admin/update" method="post"><div style="margin:0;padding:0"><input name="authenticity_token" type="hidden" value="80be9f430bb26fc4b7bf36ff77aaada31998d567" /></div>
          <input type="hidden" name="field" value="repository_description">
          <input type="text" class="textfield" name="value" value="B490 PageRank Impl">
          <div class="form-actions">
            <button class="minibutton"><span>Save</span></button> &nbsp; <a href="#" class="cancel">Cancel</a>
          </div>
        </form>
      </div>

      
      <div class="repository-homepage" id="repository_homepage" rel="repository_homepage_edit">
        <p><a href="https://github.com/grpatter/B490PageRank/wiki" rel="nofollow">https://github.com/grpatter/B490PageRank/wiki</a></p>
      </div>

      <div id="repository_homepage_edit" style="display:none;" class="inline-edit">
        <form action="/grpatter/B490PageRank/admin/update" method="post"><div style="margin:0;padding:0"><input name="authenticity_token" type="hidden" value="80be9f430bb26fc4b7bf36ff77aaada31998d567" /></div>
          <input type="hidden" name="field" value="repository_homepage">
          <input type="text" class="textfield" name="value" value="https://github.com/grpatter/B490PageRank/wiki">
          <div class="form-actions">
            <button class="minibutton"><span>Save</span></button> &nbsp; <a href="#" class="cancel">Cancel</a>
          </div>
        </form>
      </div>
      </div>
      <div class="rule "></div>
            <div id="url_box" class="url-box">
        <ul class="clone-urls">
          
            
              <li id="private_clone_url"><a href="git@github.com:grpatter/B490PageRank.git" data-permissions="Read+Write">SSH</a></li>
            
            <li id="http_clone_url"><a href="https://grpatter@github.com/grpatter/B490PageRank.git" data-permissions="Read+Write">HTTP</a></li>
            <li id="public_clone_url"><a href="git://github.com/grpatter/B490PageRank.git" data-permissions="Read-Only">Git Read-Only</a></li>
          
          
        </ul>
        <input type="text" spellcheck="false" id="url_field" class="url-field" />
              <span style="display:none" id="url_box_clippy"></span>
      <span id="clippy_tooltip_url_box_clippy" class="clippy-tooltip tooltipped" title="copy to clipboard">
      <object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000"
              width="14"
              height="14"
              class="clippy"
              id="clippy" >
      <param name="movie" value="https://d3nwyuy0nl342s.cloudfront.net/flash/clippy.swf?v5"/>
      <param name="allowScriptAccess" value="always" />
      <param name="quality" value="high" />
      <param name="scale" value="noscale" />
      <param NAME="FlashVars" value="id=url_box_clippy&amp;copied=&amp;copyto=">
      <param name="bgcolor" value="#FFFFFF">
      <param name="wmode" value="opaque">
      <embed src="https://d3nwyuy0nl342s.cloudfront.net/flash/clippy.swf?v5"
             width="14"
             height="14"
             name="clippy"
             quality="high"
             allowScriptAccess="always"
             type="application/x-shockwave-flash"
             pluginspage="http://www.macromedia.com/go/getflashplayer"
             FlashVars="id=url_box_clippy&amp;copied=&amp;copyto="
             bgcolor="#FFFFFF"
             wmode="opaque"
      />
      </object>
      </span>

        <p id="url_description">This URL has <strong>Read+Write</strong> access</p>
      </div>
    </div>


        

      </div><!-- /.pagehead -->

      

  





<script type="text/javascript">
  GitHub.downloadRepo = '/grpatter/B490PageRank/archives/c97f6bf2fb2f74222c2d46593c440cd32124d1a7'
  GitHub.revType = "SHA1"

  GitHub.controllerName = "blob"
  GitHub.actionName     = "show"
  GitHub.currentAction  = "blob#show"

  
    GitHub.hasWriteAccess = true
    GitHub.hasAdminAccess = true
    GitHub.watchingRepo = true
    GitHub.ignoredRepo = false
    GitHub.hasForkOfRepo = null
    GitHub.hasForked = true
  

  
</script>






<div class="flash-messages"></div>


  <div id="commit">
    <div class="group">
        
  <div class="envelope commit">
    <div class="human">
      
        <div class="message"><pre><a href="/grpatter/B490PageRank/commit/c97f6bf2fb2f74222c2d46593c440cd32124d1a7">added updated pagerank and perf logger</a> </pre></div>
      

      <div class="actor">
        <div class="gravatar">
          
          <img src="https://secure.gravatar.com/avatar/af8dcbcc76b84570ffc999f4f514fdff?s=140&d=https://d3nwyuy0nl342s.cloudfront.net%2Fimages%2Fgravatars%2Fgravatar-140.png" alt="" width="30" height="30"  />
        </div>
        <div class="name"><a href="/grpatter">grpatter</a> <span>(author)</span></div>
        <div class="date">
          <abbr class="relatize" title="2011-03-21 12:45:03">Mon Mar 21 12:45:03 -0700 2011</abbr>
        </div>
      </div>

      

    </div>
    <div class="machine">
      <span>c</span>ommit&nbsp;&nbsp;<a href="/grpatter/B490PageRank/commit/c97f6bf2fb2f74222c2d46593c440cd32124d1a7" hotkey="c">c97f6bf2fb2f74222c2d</a><br />
      <span>t</span>ree&nbsp;&nbsp;&nbsp;&nbsp;<a href="/grpatter/B490PageRank/tree/c97f6bf2fb2f74222c2d46593c440cd32124d1a7/Project2/doc/MPI_README.txt" hotkey="t">fb507e0a234a6d91d5ee</a><br />
      
        <span>p</span>arent&nbsp;
        
        <a href="/grpatter/B490PageRank/commit/3ebf236237acc13306505115a503a668fadaefcf" hotkey="p">3ebf236237acc1330650</a>
      

    </div>
  </div>

    </div>
  </div>



  <div id="slider">

  

    <div class="breadcrumb" data-path="Project2/src/deprecated/MpiPagerank.java/">
      <b><a href="/grpatter/B490PageRank/tree/c97f6bf2fb2f74222c2d46593c440cd32124d1a7">B490PageRank</a></b> / <a href="/grpatter/B490PageRank/tree/c97f6bf2fb2f74222c2d46593c440cd32124d1a7/Project2">Project2</a> / <a href="/grpatter/B490PageRank/tree/c97f6bf2fb2f74222c2d46593c440cd32124d1a7/Project2/src">src</a> / <a href="/grpatter/B490PageRank/tree/c97f6bf2fb2f74222c2d46593c440cd32124d1a7/Project2/src/deprecated">deprecated</a> / MpiPagerank.java       <span style="display:none" id="clippy_2765">Project2/src/deprecated/MpiPagerank.java</span>
      
      <object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000"
              width="110"
              height="14"
              class="clippy"
              id="clippy" >
      <param name="movie" value="https://d3nwyuy0nl342s.cloudfront.net/flash/clippy.swf?v5"/>
      <param name="allowScriptAccess" value="always" />
      <param name="quality" value="high" />
      <param name="scale" value="noscale" />
      <param NAME="FlashVars" value="id=clippy_2765&amp;copied=copied!&amp;copyto=copy to clipboard">
      <param name="bgcolor" value="#FFFFFF">
      <param name="wmode" value="opaque">
      <embed src="https://d3nwyuy0nl342s.cloudfront.net/flash/clippy.swf?v5"
             width="110"
             height="14"
             name="clippy"
             quality="high"
             allowScriptAccess="always"
             type="application/x-shockwave-flash"
             pluginspage="http://www.macromedia.com/go/getflashplayer"
             FlashVars="id=clippy_2765&amp;copied=copied!&amp;copyto=copy to clipboard"
             bgcolor="#FFFFFF"
             wmode="opaque"
      />
      </object>
      

    </div>

    <div class="frames">
      <div class="frame frame-center" data-path="Project2/src/deprecated/MpiPagerank.java/">
        
          <ul class="big-actions">
            
            <li><a class="file-edit-link minibutton" href="/grpatter/B490PageRank/file-edit/__current_ref__/Project2/src/deprecated/MpiPagerank.java"><span>Edit this file</span></a></li>
          </ul>
        

        <div id="files">
          <div class="file">
            <div class="meta">
              <div class="info">
                <span class="icon"><img alt="Txt" height="16" src="https://d3nwyuy0nl342s.cloudfront.net/images/icons/txt.png" width="16" /></span>
                <span class="mode" title="File Mode">100644</span>
                
                  <span>191 lines (155 sloc)</span>
                
                <span>6.258 kb</span>
              </div>
              <ul class="actions">
                <li><a href="/grpatter/B490PageRank/raw/c97f6bf2fb2f74222c2d46593c440cd32124d1a7/Project2/src/deprecated/MpiPagerank.java" id="raw-url">raw</a></li>
                
                  <li><a href="/grpatter/B490PageRank/blame/c97f6bf2fb2f74222c2d46593c440cd32124d1a7/Project2/src/deprecated/MpiPagerank.java">blame</a></li>
                
                <li><a href="/grpatter/B490PageRank/commits/c97f6bf2fb2f74222c2d46593c440cd32124d1a7/Project2/src/deprecated/MpiPagerank.java">history</a></li>
              </ul>
            </div>
            
  <div class="data type-java">
    
      <table cellpadding="0" cellspacing="0">
        <tr>
          <td>
            <pre class="line_numbers"><span id="L1" rel="#L1">1</span>
<span id="L2" rel="#L2">2</span>
<span id="L3" rel="#L3">3</span>
<span id="L4" rel="#L4">4</span>
<span id="L5" rel="#L5">5</span>
<span id="L6" rel="#L6">6</span>
<span id="L7" rel="#L7">7</span>
<span id="L8" rel="#L8">8</span>
<span id="L9" rel="#L9">9</span>
<span id="L10" rel="#L10">10</span>
<span id="L11" rel="#L11">11</span>
<span id="L12" rel="#L12">12</span>
<span id="L13" rel="#L13">13</span>
<span id="L14" rel="#L14">14</span>
<span id="L15" rel="#L15">15</span>
<span id="L16" rel="#L16">16</span>
<span id="L17" rel="#L17">17</span>
<span id="L18" rel="#L18">18</span>
<span id="L19" rel="#L19">19</span>
<span id="L20" rel="#L20">20</span>
<span id="L21" rel="#L21">21</span>
<span id="L22" rel="#L22">22</span>
<span id="L23" rel="#L23">23</span>
<span id="L24" rel="#L24">24</span>
<span id="L25" rel="#L25">25</span>
<span id="L26" rel="#L26">26</span>
<span id="L27" rel="#L27">27</span>
<span id="L28" rel="#L28">28</span>
<span id="L29" rel="#L29">29</span>
<span id="L30" rel="#L30">30</span>
<span id="L31" rel="#L31">31</span>
<span id="L32" rel="#L32">32</span>
<span id="L33" rel="#L33">33</span>
<span id="L34" rel="#L34">34</span>
<span id="L35" rel="#L35">35</span>
<span id="L36" rel="#L36">36</span>
<span id="L37" rel="#L37">37</span>
<span id="L38" rel="#L38">38</span>
<span id="L39" rel="#L39">39</span>
<span id="L40" rel="#L40">40</span>
<span id="L41" rel="#L41">41</span>
<span id="L42" rel="#L42">42</span>
<span id="L43" rel="#L43">43</span>
<span id="L44" rel="#L44">44</span>
<span id="L45" rel="#L45">45</span>
<span id="L46" rel="#L46">46</span>
<span id="L47" rel="#L47">47</span>
<span id="L48" rel="#L48">48</span>
<span id="L49" rel="#L49">49</span>
<span id="L50" rel="#L50">50</span>
<span id="L51" rel="#L51">51</span>
<span id="L52" rel="#L52">52</span>
<span id="L53" rel="#L53">53</span>
<span id="L54" rel="#L54">54</span>
<span id="L55" rel="#L55">55</span>
<span id="L56" rel="#L56">56</span>
<span id="L57" rel="#L57">57</span>
<span id="L58" rel="#L58">58</span>
<span id="L59" rel="#L59">59</span>
<span id="L60" rel="#L60">60</span>
<span id="L61" rel="#L61">61</span>
<span id="L62" rel="#L62">62</span>
<span id="L63" rel="#L63">63</span>
<span id="L64" rel="#L64">64</span>
<span id="L65" rel="#L65">65</span>
<span id="L66" rel="#L66">66</span>
<span id="L67" rel="#L67">67</span>
<span id="L68" rel="#L68">68</span>
<span id="L69" rel="#L69">69</span>
<span id="L70" rel="#L70">70</span>
<span id="L71" rel="#L71">71</span>
<span id="L72" rel="#L72">72</span>
<span id="L73" rel="#L73">73</span>
<span id="L74" rel="#L74">74</span>
<span id="L75" rel="#L75">75</span>
<span id="L76" rel="#L76">76</span>
<span id="L77" rel="#L77">77</span>
<span id="L78" rel="#L78">78</span>
<span id="L79" rel="#L79">79</span>
<span id="L80" rel="#L80">80</span>
<span id="L81" rel="#L81">81</span>
<span id="L82" rel="#L82">82</span>
<span id="L83" rel="#L83">83</span>
<span id="L84" rel="#L84">84</span>
<span id="L85" rel="#L85">85</span>
<span id="L86" rel="#L86">86</span>
<span id="L87" rel="#L87">87</span>
<span id="L88" rel="#L88">88</span>
<span id="L89" rel="#L89">89</span>
<span id="L90" rel="#L90">90</span>
<span id="L91" rel="#L91">91</span>
<span id="L92" rel="#L92">92</span>
<span id="L93" rel="#L93">93</span>
<span id="L94" rel="#L94">94</span>
<span id="L95" rel="#L95">95</span>
<span id="L96" rel="#L96">96</span>
<span id="L97" rel="#L97">97</span>
<span id="L98" rel="#L98">98</span>
<span id="L99" rel="#L99">99</span>
<span id="L100" rel="#L100">100</span>
<span id="L101" rel="#L101">101</span>
<span id="L102" rel="#L102">102</span>
<span id="L103" rel="#L103">103</span>
<span id="L104" rel="#L104">104</span>
<span id="L105" rel="#L105">105</span>
<span id="L106" rel="#L106">106</span>
<span id="L107" rel="#L107">107</span>
<span id="L108" rel="#L108">108</span>
<span id="L109" rel="#L109">109</span>
<span id="L110" rel="#L110">110</span>
<span id="L111" rel="#L111">111</span>
<span id="L112" rel="#L112">112</span>
<span id="L113" rel="#L113">113</span>
<span id="L114" rel="#L114">114</span>
<span id="L115" rel="#L115">115</span>
<span id="L116" rel="#L116">116</span>
<span id="L117" rel="#L117">117</span>
<span id="L118" rel="#L118">118</span>
<span id="L119" rel="#L119">119</span>
<span id="L120" rel="#L120">120</span>
<span id="L121" rel="#L121">121</span>
<span id="L122" rel="#L122">122</span>
<span id="L123" rel="#L123">123</span>
<span id="L124" rel="#L124">124</span>
<span id="L125" rel="#L125">125</span>
<span id="L126" rel="#L126">126</span>
<span id="L127" rel="#L127">127</span>
<span id="L128" rel="#L128">128</span>
<span id="L129" rel="#L129">129</span>
<span id="L130" rel="#L130">130</span>
<span id="L131" rel="#L131">131</span>
<span id="L132" rel="#L132">132</span>
<span id="L133" rel="#L133">133</span>
<span id="L134" rel="#L134">134</span>
<span id="L135" rel="#L135">135</span>
<span id="L136" rel="#L136">136</span>
<span id="L137" rel="#L137">137</span>
<span id="L138" rel="#L138">138</span>
<span id="L139" rel="#L139">139</span>
<span id="L140" rel="#L140">140</span>
<span id="L141" rel="#L141">141</span>
<span id="L142" rel="#L142">142</span>
<span id="L143" rel="#L143">143</span>
<span id="L144" rel="#L144">144</span>
<span id="L145" rel="#L145">145</span>
<span id="L146" rel="#L146">146</span>
<span id="L147" rel="#L147">147</span>
<span id="L148" rel="#L148">148</span>
<span id="L149" rel="#L149">149</span>
<span id="L150" rel="#L150">150</span>
<span id="L151" rel="#L151">151</span>
<span id="L152" rel="#L152">152</span>
<span id="L153" rel="#L153">153</span>
<span id="L154" rel="#L154">154</span>
<span id="L155" rel="#L155">155</span>
<span id="L156" rel="#L156">156</span>
<span id="L157" rel="#L157">157</span>
<span id="L158" rel="#L158">158</span>
<span id="L159" rel="#L159">159</span>
<span id="L160" rel="#L160">160</span>
<span id="L161" rel="#L161">161</span>
<span id="L162" rel="#L162">162</span>
<span id="L163" rel="#L163">163</span>
<span id="L164" rel="#L164">164</span>
<span id="L165" rel="#L165">165</span>
<span id="L166" rel="#L166">166</span>
<span id="L167" rel="#L167">167</span>
<span id="L168" rel="#L168">168</span>
<span id="L169" rel="#L169">169</span>
<span id="L170" rel="#L170">170</span>
<span id="L171" rel="#L171">171</span>
<span id="L172" rel="#L172">172</span>
<span id="L173" rel="#L173">173</span>
<span id="L174" rel="#L174">174</span>
<span id="L175" rel="#L175">175</span>
<span id="L176" rel="#L176">176</span>
<span id="L177" rel="#L177">177</span>
<span id="L178" rel="#L178">178</span>
<span id="L179" rel="#L179">179</span>
<span id="L180" rel="#L180">180</span>
<span id="L181" rel="#L181">181</span>
<span id="L182" rel="#L182">182</span>
<span id="L183" rel="#L183">183</span>
<span id="L184" rel="#L184">184</span>
<span id="L185" rel="#L185">185</span>
<span id="L186" rel="#L186">186</span>
<span id="L187" rel="#L187">187</span>
<span id="L188" rel="#L188">188</span>
<span id="L189" rel="#L189">189</span>
<span id="L190" rel="#L190">190</span>
<span id="L191" rel="#L191">191</span>
</pre>
          </td>
          <td width="100%">
            
              
                <div class="highlight"><pre><div class='line' id='LC1'><span class="kn">import</span> <span class="nn">java.io.*</span><span class="o">;</span></div><div class='line' id='LC2'><span class="kn">import</span> <span class="nn">java.security.AllPermission</span><span class="o">;</span></div><div class='line' id='LC3'><span class="kn">import</span> <span class="nn">java.util.*</span><span class="o">;</span></div><div class='line' id='LC4'><span class="kn">import</span> <span class="nn">mpi.*</span><span class="o">;</span></div><div class='line' id='LC5'><span class="kn">import</span> <span class="nn">mpi.Intracomm.*</span><span class="o">;</span></div><div class='line' id='LC6'><br/></div><div class='line' id='LC7'><br/></div><div class='line' id='LC8'><span class="kd">public</span> <span class="kd">class</span> <span class="nc">MpiPagerank</span> <span class="o">{</span></div><div class='line' id='LC9'><br/></div><div class='line' id='LC10'>	<span class="kd">public</span> <span class="kd">static</span> <span class="kt">int</span> <span class="nf">readPagerankInput</span><span class="o">(</span><span class="n">String</span> <span class="n">file</span><span class="o">,</span> <span class="kt">int</span><span class="o">[][]</span> <span class="n">AdjacencyMatrix</span><span class="o">)</span> <span class="kd">throws</span> <span class="n">NumberFormatException</span><span class="o">,</span> <span class="n">IOException</span> <span class="o">{</span></div><div class='line' id='LC11'><br/></div><div class='line' id='LC12'>	    <span class="n">BufferedReader</span> <span class="n">f</span> <span class="o">=</span> <span class="k">new</span> <span class="n">BufferedReader</span><span class="o">(</span><span class="k">new</span> <span class="n">FileReader</span><span class="o">(</span><span class="n">file</span><span class="o">));</span></div><div class='line' id='LC13'>	    <span class="kt">int</span> <span class="n">totalUrlCount</span> <span class="o">=</span> <span class="mi">0</span><span class="o">;</span></div><div class='line' id='LC14'>	    <span class="kt">int</span> <span class="n">maxLineLength</span> <span class="o">=</span> <span class="mi">0</span><span class="o">;</span></div><div class='line' id='LC15'><br/></div><div class='line' id='LC16'>	    <span class="k">while</span> <span class="o">(</span><span class="n">f</span><span class="o">.</span><span class="na">ready</span><span class="o">())</span> <span class="o">{</span></div><div class='line' id='LC17'>		<span class="n">String</span><span class="o">[]</span> <span class="n">adjacencyInfo</span> <span class="o">=</span> <span class="n">f</span><span class="o">.</span><span class="na">readLine</span><span class="o">().</span><span class="na">split</span><span class="o">(</span><span class="s">&quot; &quot;</span><span class="o">);</span></div><div class='line' id='LC18'>		<span class="kt">int</span> <span class="n">infoLength</span> <span class="o">=</span> <span class="n">adjacencyInfo</span><span class="o">.</span><span class="na">length</span><span class="o">;</span></div><div class='line' id='LC19'>		<span class="n">totalUrlCount</span><span class="o">++;</span></div><div class='line' id='LC20'><br/></div><div class='line' id='LC21'>		<span class="k">if</span> <span class="o">(</span><span class="n">infoLength</span> <span class="o">&gt;</span> <span class="n">maxLineLength</span><span class="o">)</span> <span class="o">{</span></div><div class='line' id='LC22'>		    <span class="n">maxLineLength</span> <span class="o">=</span> <span class="n">infoLength</span><span class="o">;</span></div><div class='line' id='LC23'>		<span class="o">}</span></div><div class='line' id='LC24'>	    <span class="o">}</span></div><div class='line' id='LC25'><br/></div><div class='line' id='LC26'>	    <span class="k">for</span> <span class="o">(</span><span class="kt">int</span> <span class="n">i</span> <span class="o">=</span> <span class="mi">0</span><span class="o">;</span> <span class="n">i</span> <span class="o">&lt;</span> <span class="n">totalUrlCount</span><span class="o">;</span> <span class="n">i</span><span class="o">++)</span> <span class="o">{</span></div><div class='line' id='LC27'>		<span class="k">for</span> <span class="o">(</span><span class="kt">int</span> <span class="n">j</span> <span class="o">=</span> <span class="mi">0</span><span class="o">;</span> <span class="n">j</span> <span class="o">&lt;</span> <span class="n">maxLineLength</span><span class="o">;</span> <span class="n">j</span><span class="o">++)</span> <span class="o">{</span></div><div class='line' id='LC28'>		    <span class="n">AdjacencyMatrix</span><span class="o">[</span><span class="n">i</span><span class="o">][</span><span class="n">j</span><span class="o">]</span> <span class="o">=</span> <span class="o">-</span><span class="mi">1</span><span class="o">;</span></div><div class='line' id='LC29'>		<span class="o">}</span></div><div class='line' id='LC30'>	    <span class="o">}</span></div><div class='line' id='LC31'><br/></div><div class='line' id='LC32'>	    <span class="n">f</span><span class="o">.</span><span class="na">close</span><span class="o">();</span></div><div class='line' id='LC33'>	    <span class="n">f</span> <span class="o">=</span> <span class="k">new</span> <span class="n">BufferedReader</span><span class="o">(</span><span class="k">new</span> <span class="n">FileReader</span><span class="o">(</span><span class="n">file</span><span class="o">));</span></div><div class='line' id='LC34'><br/></div><div class='line' id='LC35'>	    <span class="kt">int</span> <span class="n">amPointer</span> <span class="o">=</span> <span class="mi">0</span><span class="o">;</span></div><div class='line' id='LC36'>	    <span class="k">while</span> <span class="o">(</span><span class="n">f</span><span class="o">.</span><span class="na">ready</span><span class="o">())</span> <span class="o">{</span></div><div class='line' id='LC37'>		<span class="n">String</span><span class="o">[]</span> <span class="n">adjacencyInfo</span> <span class="o">=</span> <span class="n">f</span><span class="o">.</span><span class="na">readLine</span><span class="o">().</span><span class="na">split</span><span class="o">(</span><span class="s">&quot; &quot;</span><span class="o">);</span></div><div class='line' id='LC38'><br/></div><div class='line' id='LC39'>		<span class="k">for</span> <span class="o">(</span><span class="kt">int</span> <span class="n">i</span> <span class="o">=</span> <span class="mi">0</span><span class="o">;</span> <span class="n">i</span> <span class="o">&lt;</span> <span class="n">adjacencyInfo</span><span class="o">.</span><span class="na">length</span><span class="o">;</span> <span class="n">i</span><span class="o">++)</span> <span class="o">{</span></div><div class='line' id='LC40'>		    <span class="n">AdjacencyMatrix</span><span class="o">[</span><span class="n">amPointer</span><span class="o">][</span><span class="n">i</span><span class="o">]</span> <span class="o">=</span> <span class="n">Integer</span><span class="o">.</span><span class="na">parseInt</span><span class="o">(</span><span class="n">adjacencyInfo</span><span class="o">[</span><span class="n">i</span><span class="o">]);</span></div><div class='line' id='LC41'>		<span class="o">}</span></div><div class='line' id='LC42'>		<span class="n">amPointer</span><span class="o">++;</span></div><div class='line' id='LC43'>	    <span class="o">}</span></div><div class='line' id='LC44'><br/></div><div class='line' id='LC45'>	    <span class="n">f</span><span class="o">.</span><span class="na">close</span><span class="o">();</span></div><div class='line' id='LC46'>	    <span class="k">return</span> <span class="n">totalUrlCount</span><span class="o">;</span></div><div class='line' id='LC47'>	<span class="o">}</span></div><div class='line' id='LC48'><br/></div><div class='line' id='LC49'>	<span class="cm">/**</span></div><div class='line' id='LC50'><span class="cm">	 * @param args</span></div><div class='line' id='LC51'><span class="cm">	 * @throws IOException </span></div><div class='line' id='LC52'><span class="cm">	 * @throws ClassNotFoundException </span></div><div class='line' id='LC53'><span class="cm">	 */</span></div><div class='line' id='LC54'>	<span class="kd">public</span> <span class="kd">static</span> <span class="kt">void</span> <span class="nf">main</span><span class="o">(</span><span class="n">String</span><span class="o">[]</span> <span class="n">args</span><span class="o">)</span> <span class="kd">throws</span> <span class="n">IOException</span><span class="o">,</span> <span class="n">ClassNotFoundException</span> <span class="o">{</span></div><div class='line' id='LC55'>		<span class="n">MPI</span><span class="o">.</span><span class="na">Init</span><span class="o">(</span><span class="n">args</span><span class="o">);</span></div><div class='line' id='LC56'>		<span class="kt">int</span> <span class="n">nodeId</span> <span class="o">=</span> <span class="n">MPI</span><span class="o">.</span><span class="na">COMM_WORLD</span><span class="o">.</span><span class="na">Rank</span><span class="o">();</span></div><div class='line' id='LC57'>		<span class="kt">int</span> <span class="n">globalNodeCount</span> <span class="o">=</span> <span class="n">MPI</span><span class="o">.</span><span class="na">COMM_WORLD</span><span class="o">.</span><span class="na">Size</span><span class="o">();</span></div><div class='line' id='LC58'>		<span class="n">String</span> <span class="n">pagerankInputFilename</span> <span class="o">=</span> <span class="s">&quot;C:\\pagerank.input&quot;</span><span class="o">;</span></div><div class='line' id='LC59'>		<span class="kt">int</span> <span class="n">urlCount</span> <span class="o">=</span> <span class="mi">0</span><span class="o">;</span></div><div class='line' id='LC60'><span class="c1">//		Double PublicPR[] = new Double[1000];</span></div><div class='line' id='LC61'><span class="c1">//		Double PrivatePR[] = new Double[1000];</span></div><div class='line' id='LC62'>		<span class="kt">double</span> <span class="n">PublicPR</span><span class="o">[]</span> <span class="o">=</span> <span class="k">new</span> <span class="kt">double</span><span class="o">[</span><span class="mi">1000</span><span class="o">];</span></div><div class='line' id='LC63'>		<span class="kt">double</span> <span class="n">PrivatePR</span><span class="o">[]</span> <span class="o">=</span> <span class="k">new</span> <span class="kt">double</span><span class="o">[</span><span class="mi">1000</span><span class="o">];</span></div><div class='line' id='LC64'>		<span class="kt">int</span><span class="o">[][]</span> <span class="n">globalAdjacencyMatrix</span> <span class="o">=</span> <span class="k">new</span> <span class="kt">int</span><span class="o">[</span><span class="mi">1000</span><span class="o">][</span><span class="mi">100</span><span class="o">];</span></div><div class='line' id='LC65'>		<span class="n">Double</span> <span class="n">dangling</span> <span class="o">=</span> <span class="mf">0.0</span><span class="o">;</span></div><div class='line' id='LC66'>		<span class="n">Intracomm</span> <span class="n">mpiComm</span> <span class="o">=</span> <span class="n">MPI</span><span class="o">.</span><span class="na">COMM_WORLD</span><span class="o">;</span></div><div class='line' id='LC67'>		<span class="kt">int</span> <span class="n">iteration</span> <span class="o">=</span> <span class="mi">1</span><span class="o">;</span></div><div class='line' id='LC68'>		<span class="c1">// Read in adjacency matrix from file</span></div><div class='line' id='LC69'>		<span class="n">urlCount</span> <span class="o">=</span> <span class="n">readPagerankInput</span><span class="o">(</span><span class="n">pagerankInputFilename</span><span class="o">,</span> <span class="n">globalAdjacencyMatrix</span><span class="o">);</span></div><div class='line' id='LC70'>		<span class="kt">int</span> <span class="n">localUrlCount</span> <span class="o">=</span> <span class="n">urlCount</span> <span class="o">/</span> <span class="n">globalNodeCount</span><span class="o">;</span></div><div class='line' id='LC71'><br/></div><div class='line' id='LC72'>		<span class="c1">//Populate the initial pagerank</span></div><div class='line' id='LC73'>		<span class="k">for</span> <span class="o">(</span><span class="kt">int</span> <span class="n">m</span> <span class="o">=</span> <span class="mi">0</span><span class="o">;</span> <span class="n">m</span> <span class="o">&lt;</span> <span class="n">globalAdjacencyMatrix</span><span class="o">.</span><span class="na">length</span><span class="o">;</span> <span class="n">m</span><span class="o">++)</span> <span class="o">{</span></div><div class='line' id='LC74'>		    <span class="n">PublicPR</span><span class="o">[</span><span class="n">m</span><span class="o">]</span> <span class="o">=</span> <span class="mf">1.0</span><span class="o">/</span><span class="n">urlCount</span><span class="o">;</span>  </div><div class='line' id='LC75'>		    <span class="n">PrivatePR</span><span class="o">[</span><span class="n">m</span><span class="o">]</span> <span class="o">=</span> <span class="mf">1.0</span><span class="o">/</span><span class="n">urlCount</span><span class="o">;</span>	</div><div class='line' id='LC76'><br/></div><div class='line' id='LC77'>		<span class="o">}</span></div><div class='line' id='LC78'><br/></div><div class='line' id='LC79'><br/></div><div class='line' id='LC80'>		<span class="k">for</span> <span class="o">(</span><span class="kt">int</span> <span class="n">iter</span> <span class="o">=</span> <span class="mi">0</span><span class="o">;</span> <span class="n">iter</span> <span class="o">&lt;</span> <span class="n">iteration</span><span class="o">;</span> <span class="n">iter</span><span class="o">++)</span> <span class="o">{</span></div><div class='line' id='LC81'>		    <span class="kt">int</span> <span class="n">tracker</span> <span class="o">=</span> <span class="mi">1</span><span class="o">;</span>			    </div><div class='line' id='LC82'><br/></div><div class='line' id='LC83'>		    <span class="k">if</span> <span class="o">(</span><span class="n">nodeId</span> <span class="o">==</span> <span class="mi">0</span><span class="o">)</span> <span class="o">{</span></div><div class='line' id='LC84'>				<span class="c1">//System.out.println(&quot;Hello from king node &quot; + nodeId + &quot;. I rule over a kingdom of &quot; + globalNodeCount + &quot; nodes.&quot;);</span></div><div class='line' id='LC85'><br/></div><div class='line' id='LC86'>				<span class="k">while</span> <span class="o">(</span><span class="n">tracker</span> <span class="o">&lt;=</span> <span class="n">localUrlCount</span><span class="o">)</span> <span class="o">{</span></div><div class='line' id='LC87'>				    <span class="k">for</span> <span class="o">(</span><span class="kt">int</span> <span class="n">j</span> <span class="o">=</span> <span class="mi">1</span><span class="o">;</span> <span class="n">j</span> <span class="o">&lt;</span> <span class="n">globalNodeCount</span><span class="o">;</span> <span class="n">j</span><span class="o">++)</span> <span class="o">{</span></div><div class='line' id='LC88'>						<span class="kt">int</span> <span class="n">toBeSent</span><span class="o">[][]</span> <span class="o">=</span> <span class="k">new</span> <span class="kt">int</span><span class="o">[</span><span class="n">globalAdjacencyMatrix</span><span class="o">.</span><span class="na">length</span> <span class="o">/</span> <span class="n">globalNodeCount</span><span class="o">][</span><span class="n">globalAdjacencyMatrix</span><span class="o">[</span><span class="mi">0</span><span class="o">].</span><span class="na">length</span><span class="o">];</span></div><div class='line' id='LC89'><br/></div><div class='line' id='LC90'>						<span class="k">for</span> <span class="o">(</span><span class="kt">int</span> <span class="n">k</span> <span class="o">=</span> <span class="mi">0</span><span class="o">;</span> <span class="n">k</span> <span class="o">&lt;</span> <span class="n">globalAdjacencyMatrix</span><span class="o">[</span><span class="mi">0</span><span class="o">].</span><span class="na">length</span><span class="o">;</span> <span class="n">k</span><span class="o">++)</span> <span class="o">{</span></div><div class='line' id='LC91'>						    <span class="n">toBeSent</span><span class="o">[</span><span class="n">j</span> <span class="o">-</span> <span class="mi">1</span><span class="o">][</span><span class="n">k</span><span class="o">]</span> <span class="o">=</span> <span class="n">globalAdjacencyMatrix</span><span class="o">[(</span><span class="n">j</span> <span class="o">*</span> <span class="n">tracker</span><span class="o">)</span> <span class="o">-</span> <span class="mi">1</span><span class="o">][</span><span class="n">k</span><span class="o">];</span> </div><div class='line' id='LC92'>						<span class="o">}</span></div><div class='line' id='LC93'><br/></div><div class='line' id='LC94'>						<span class="n">System</span><span class="o">.</span><span class="na">out</span><span class="o">.</span><span class="na">println</span><span class="o">(</span><span class="s">&quot;This is the tracker: &quot;</span> <span class="o">+</span> <span class="n">tracker</span><span class="o">);</span></div><div class='line' id='LC95'>						<span class="n">System</span><span class="o">.</span><span class="na">out</span><span class="o">.</span><span class="na">println</span><span class="o">(</span><span class="s">&quot;This is the j: &quot;</span> <span class="o">+</span> <span class="n">j</span><span class="o">);</span></div><div class='line' id='LC96'>						<span class="n">Object</span> <span class="n">ad</span> <span class="o">=</span> <span class="o">(</span><span class="n">Object</span><span class="o">)</span><span class="n">toBeSent</span><span class="o">;</span></div><div class='line' id='LC97'>						<span class="n">Object</span> <span class="n">send</span><span class="o">[]</span> <span class="o">=</span> <span class="k">new</span> <span class="n">Object</span><span class="o">[</span><span class="mi">1</span><span class="o">];</span></div><div class='line' id='LC98'>						<span class="n">send</span><span class="o">[</span><span class="mi">0</span><span class="o">]</span> <span class="o">=</span> <span class="n">ad</span><span class="o">;</span></div><div class='line' id='LC99'>						<span class="n">System</span><span class="o">.</span><span class="na">out</span><span class="o">.</span><span class="na">println</span><span class="o">(</span><span class="s">&quot;Sending from: &quot;</span> <span class="o">+</span> <span class="n">nodeId</span> <span class="o">+</span> <span class="s">&quot; to: &quot;</span> <span class="o">+</span> <span class="n">tracker</span> <span class="o">+</span> <span class="s">&quot; a set of size: &quot;</span> <span class="o">+</span> <span class="n">toBeSent</span><span class="o">.</span><span class="na">length</span><span class="o">);</span>			</div><div class='line' id='LC100'>						<span class="n">mpiComm</span><span class="o">.</span><span class="na">Send</span><span class="o">(</span><span class="n">send</span><span class="o">,</span> <span class="mi">0</span><span class="o">,</span> <span class="n">send</span><span class="o">.</span><span class="na">length</span><span class="o">,</span> <span class="n">MPI</span><span class="o">.</span><span class="na">OBJECT</span><span class="o">,</span> <span class="n">j</span><span class="o">,</span> <span class="mi">1</span><span class="o">);</span>			    </div><div class='line' id='LC101'>				    <span class="o">}</span></div><div class='line' id='LC102'>				    <span class="n">tracker</span><span class="o">++;</span></div><div class='line' id='LC103'>				    <span class="k">break</span><span class="o">;</span></div><div class='line' id='LC104'>				<span class="o">}</span></div><div class='line' id='LC105'><br/></div><div class='line' id='LC106'>		    <span class="o">}</span> <span class="k">else</span> <span class="o">{</span></div><div class='line' id='LC107'>				<span class="n">System</span><span class="o">.</span><span class="na">out</span><span class="o">.</span><span class="na">println</span><span class="o">(</span><span class="s">&quot;I&#39;m just a worker node named &quot;</span> <span class="o">+</span> <span class="n">nodeId</span><span class="o">);</span></div><div class='line' id='LC108'><br/></div><div class='line' id='LC109'>				<span class="c1">// TODO Receive data from root.</span></div><div class='line' id='LC110'>				<span class="n">Object</span> <span class="n">in</span><span class="o">[]</span> <span class="o">=</span> <span class="k">new</span> <span class="n">Object</span><span class="o">[</span><span class="mi">1</span><span class="o">];</span></div><div class='line' id='LC111'>				<span class="n">System</span><span class="o">.</span><span class="na">out</span><span class="o">.</span><span class="na">println</span><span class="o">(</span><span class="s">&quot;Recieving...&quot;</span><span class="o">);</span></div><div class='line' id='LC112'>				<span class="n">mpiComm</span><span class="o">.</span><span class="na">Recv</span><span class="o">(</span><span class="n">in</span><span class="o">,</span> <span class="mi">0</span><span class="o">,</span> <span class="mi">1</span><span class="o">,</span> <span class="n">MPI</span><span class="o">.</span><span class="na">OBJECT</span><span class="o">,</span> <span class="mi">0</span><span class="o">,</span> <span class="n">MPI</span><span class="o">.</span><span class="na">ANY_TAG</span><span class="o">);</span></div><div class='line' id='LC113'>				<span class="n">System</span><span class="o">.</span><span class="na">out</span><span class="o">.</span><span class="na">println</span><span class="o">(</span><span class="s">&quot;Recieved&quot;</span><span class="o">);</span></div><div class='line' id='LC114'>				<span class="kt">int</span><span class="o">[][]</span> <span class="n">setToWork</span> <span class="o">=</span> <span class="k">new</span> <span class="kt">int</span><span class="o">[</span><span class="mi">1</span><span class="o">][</span><span class="mi">1</span><span class="o">];</span></div><div class='line' id='LC115'>				<span class="n">setToWork</span> <span class="o">=</span> <span class="o">(</span><span class="kt">int</span><span class="o">[][])</span><span class="n">in</span><span class="o">[</span><span class="mi">0</span><span class="o">];</span><span class="c1">//from object to 2d array</span></div><div class='line' id='LC116'><br/></div><div class='line' id='LC117'>				<span class="c1">//System.out.print(setToWork[0][1]);</span></div><div class='line' id='LC118'>				<span class="k">for</span><span class="o">(</span><span class="kt">int</span> <span class="n">x</span> <span class="o">=</span> <span class="mi">0</span><span class="o">;</span> <span class="n">x</span> <span class="o">&lt;</span> <span class="n">setToWork</span><span class="o">.</span><span class="na">length</span><span class="o">;</span> <span class="n">x</span><span class="o">++){</span></div><div class='line' id='LC119'>				    <span class="kt">int</span><span class="o">[]</span> <span class="n">currentLinks</span> <span class="o">=</span> <span class="n">setToWork</span><span class="o">[</span><span class="n">x</span><span class="o">];</span></div><div class='line' id='LC120'>				    <span class="kt">int</span> <span class="n">outgoingLinkCount</span> <span class="o">=</span> <span class="n">currentLinks</span><span class="o">.</span><span class="na">length</span><span class="o">;</span>			</div><div class='line' id='LC121'>				    <span class="c1">//Iterate through each of this webpage&#39;s neighbors and calculate their Pagerank based on this web pages current Pagerank and number of outgoing links</span></div><div class='line' id='LC122'>				    <span class="k">for</span><span class="o">(</span><span class="kt">int</span> <span class="n">j</span> <span class="o">=</span> <span class="mi">0</span><span class="o">;</span> <span class="n">j</span> <span class="o">&lt;</span> <span class="n">outgoingLinkCount</span><span class="o">;</span> <span class="n">j</span><span class="o">++){</span></div><div class='line' id='LC123'>					<span class="kt">int</span> <span class="n">neighbor</span> <span class="o">=</span> <span class="n">currentLinks</span><span class="o">[</span><span class="n">j</span><span class="o">];</span></div><div class='line' id='LC124'>					<span class="k">if</span><span class="o">(</span><span class="n">neighbor</span> <span class="o">!=</span> <span class="o">-</span><span class="mi">1</span><span class="o">){</span></div><div class='line' id='LC125'>					    <span class="n">PrivatePR</span><span class="o">[</span><span class="n">x</span><span class="o">]</span> <span class="o">+=</span> <span class="n">PublicPR</span><span class="o">[</span><span class="n">neighbor</span><span class="o">]/</span><span class="n">outgoingLinkCount</span><span class="o">;</span>	</div><div class='line' id='LC126'><span class="c1">//					    System.out.println(&quot;Setting PrPR[&quot;+x+&quot;] to:&quot; + PrivatePR[x]);</span></div><div class='line' id='LC127'>					<span class="o">}</span> <span class="k">else</span> <span class="o">{</span> <span class="n">dangling</span> <span class="o">+=</span> <span class="n">PrivatePR</span><span class="o">[</span><span class="n">x</span><span class="o">];</span> <span class="o">}</span></div><div class='line' id='LC128'>				    <span class="o">}</span></div><div class='line' id='LC129'>				<span class="o">}</span></div><div class='line' id='LC130'>				<span class="k">for</span><span class="o">(</span><span class="kt">int</span> <span class="n">l</span> <span class="o">=</span> <span class="mi">0</span><span class="o">;</span> <span class="n">l</span> <span class="o">&lt;</span> <span class="n">urlCount</span><span class="o">;</span> <span class="n">l</span><span class="o">++)</span> <span class="o">{</span></div><div class='line' id='LC131'>				    <span class="n">System</span><span class="o">.</span><span class="na">out</span><span class="o">.</span><span class="na">println</span><span class="o">(</span><span class="s">&quot;AFTER CALCING PrPR[&quot;</span><span class="o">+</span><span class="n">l</span><span class="o">+</span><span class="s">&quot;] is: &quot;</span> <span class="o">+</span> <span class="n">PrivatePR</span><span class="o">[</span><span class="n">l</span><span class="o">]);</span>			</div><div class='line' id='LC132'>				<span class="o">}</span></div><div class='line' id='LC133'><span class="c1">//				System.out.println(&quot;dangling is: &quot; + dangling);</span></div><div class='line' id='LC134'>		    <span class="o">}</span></div><div class='line' id='LC135'><br/></div><div class='line' id='LC136'><br/></div><div class='line' id='LC137'>			<span class="k">try</span> <span class="o">{</span></div><div class='line' id='LC138'>				<span class="n">MpiPagerank</span><span class="o">.</span><span class="na">allreduceTest</span><span class="o">(</span><span class="n">args</span><span class="o">,</span> <span class="n">PrivatePR</span><span class="o">,</span> <span class="n">PublicPR</span><span class="o">,</span> <span class="n">urlCount</span><span class="o">,</span> <span class="n">dangling</span><span class="o">);</span></div><div class='line' id='LC139'>			<span class="o">}</span> <span class="k">catch</span> <span class="o">(</span><span class="n">Exception</span> <span class="n">e</span><span class="o">)</span> <span class="o">{</span></div><div class='line' id='LC140'>				<span class="n">e</span><span class="o">.</span><span class="na">printStackTrace</span><span class="o">();</span></div><div class='line' id='LC141'>			<span class="o">}</span></div><div class='line' id='LC142'><br/></div><div class='line' id='LC143'>			<span class="n">MPI</span><span class="o">.</span><span class="na">Finalize</span><span class="o">();</span></div><div class='line' id='LC144'>		<span class="o">}</span></div><div class='line' id='LC145'>	<span class="o">}</span></div><div class='line' id='LC146'><br/></div><div class='line' id='LC147'>	  <span class="kd">public</span> <span class="kd">static</span> <span class="kt">void</span> <span class="nf">allreduceTest</span><span class="o">(</span><span class="n">String</span><span class="o">[]</span> <span class="n">args</span><span class="o">,</span> <span class="kt">double</span><span class="o">[]</span> <span class="n">PrivatePR</span><span class="o">,</span> <span class="kt">double</span><span class="o">[]</span><span class="n">PublicPR</span><span class="o">,</span> <span class="kt">int</span> <span class="n">urlCount</span><span class="o">,</span> <span class="n">Double</span> <span class="n">dangling</span><span class="o">)</span> <span class="kd">throws</span> <span class="n">Exception</span> <span class="o">{</span></div><div class='line' id='LC148'><br/></div><div class='line' id='LC149'>		    <span class="kt">int</span> <span class="n">j</span><span class="o">;</span></div><div class='line' id='LC150'>		    <span class="kt">int</span> <span class="n">myself</span><span class="o">;</span></div><div class='line' id='LC151'>		    <span class="kt">int</span> <span class="n">cur</span> <span class="o">=</span> <span class="mi">0</span><span class="o">,</span><span class="n">prev</span> <span class="o">=</span> <span class="mi">0</span><span class="o">;</span></div><div class='line' id='LC152'>		    <span class="n">Double</span> <span class="n">dangPerPage</span> <span class="o">=</span> <span class="n">dangling</span><span class="o">/</span><span class="n">urlCount</span><span class="o">;</span></div><div class='line' id='LC153'>		    <span class="n">Double</span> <span class="n">damping</span> <span class="o">=</span> <span class="mf">0.85</span><span class="o">;</span> <span class="c1">//TEMP</span></div><div class='line' id='LC154'><br/></div><div class='line' id='LC155'>		    <span class="n">myself</span> <span class="o">=</span> <span class="n">MPI</span><span class="o">.</span><span class="na">COMM_WORLD</span><span class="o">.</span><span class="na">Rank</span><span class="o">();</span></div><div class='line' id='LC156'><br/></div><div class='line' id='LC157'>		    <span class="k">for</span><span class="o">(</span><span class="kt">int</span> <span class="n">a</span> <span class="o">=</span> <span class="mi">0</span><span class="o">;</span> <span class="n">a</span><span class="o">&lt;</span> <span class="mi">1000</span><span class="o">;</span> <span class="n">a</span><span class="o">++){</span></div><div class='line' id='LC158'>		    	<span class="n">System</span><span class="o">.</span><span class="na">out</span><span class="o">.</span><span class="na">println</span><span class="o">(</span><span class="s">&quot;BEFORE REDUCE node: &quot;</span> <span class="o">+</span> <span class="n">a</span> <span class="o">+</span> <span class="s">&quot; :PubPr[]/PrPr[] is: &quot;</span> <span class="o">+</span> <span class="n">PublicPR</span><span class="o">[</span><span class="n">a</span><span class="o">]</span> <span class="o">+</span> <span class="s">&quot; / &quot;</span> <span class="o">+</span> <span class="n">PrivatePR</span><span class="o">[</span><span class="n">a</span><span class="o">]);</span>		    	</div><div class='line' id='LC159'>		    <span class="o">}</span></div><div class='line' id='LC160'><br/></div><div class='line' id='LC161'>		    <span class="k">for</span><span class="o">(</span><span class="n">j</span><span class="o">=</span><span class="mi">1</span><span class="o">;</span><span class="n">j</span><span class="o">&lt;</span><span class="n">urlCount</span><span class="o">;</span><span class="n">j</span><span class="o">++)</span>  <span class="o">{</span></div><div class='line' id='LC162'>		      <span class="n">cur</span> <span class="o">=</span> <span class="n">j</span><span class="o">;</span></div><div class='line' id='LC163'><br/></div><div class='line' id='LC164'>		      <span class="n">MPI</span><span class="o">.</span><span class="na">COMM_WORLD</span><span class="o">.</span><span class="na">Allreduce</span><span class="o">(</span><span class="n">PrivatePR</span><span class="o">,</span><span class="mi">0</span><span class="o">,</span><span class="n">PublicPR</span><span class="o">,</span><span class="mi">0</span><span class="o">,</span><span class="n">j</span><span class="o">,</span><span class="n">MPI</span><span class="o">.</span><span class="na">DOUBLE</span><span class="o">,</span><span class="n">MPI</span><span class="o">.</span><span class="na">SUM</span><span class="o">);</span> <span class="c1">//Send, offset, Receive, offset, size, type, operation	      </span></div><div class='line' id='LC165'>		      <span class="n">MPI</span><span class="o">.</span><span class="na">COMM_WORLD</span><span class="o">.</span><span class="na">Barrier</span><span class="o">();</span> <span class="c1">// block the caller until all process in the group have called it.</span></div><div class='line' id='LC166'><br/></div><div class='line' id='LC167'>		      <span class="k">if</span><span class="o">(</span><span class="n">cur</span> <span class="o">!=</span> <span class="n">prev</span><span class="o">){</span></div><div class='line' id='LC168'><span class="c1">//		    	  PublicPR[j] = PublicPR[j] + dangPerPage;//apply dangling</span></div><div class='line' id='LC169'>		    	  <span class="n">System</span><span class="o">.</span><span class="na">out</span><span class="o">.</span><span class="na">println</span><span class="o">(</span><span class="s">&quot;AFTER REDUCE node: &quot;</span> <span class="o">+</span> <span class="n">j</span> <span class="o">+</span> <span class="s">&quot; :PubPr[]/PrPr[] is: &quot;</span> <span class="o">+</span> <span class="n">PublicPR</span><span class="o">[</span><span class="n">j</span><span class="o">]</span> <span class="o">+</span> <span class="s">&quot; / &quot;</span> <span class="o">+</span> <span class="n">PrivatePR</span><span class="o">[</span><span class="n">j</span><span class="o">]);</span></div><div class='line' id='LC170'>		      <span class="o">}</span><span class="k">else</span><span class="o">{</span></div><div class='line' id='LC171'>		    	  <span class="n">prev</span> <span class="o">=</span> <span class="n">cur</span><span class="o">;</span></div><div class='line' id='LC172'>		    	  <span class="n">cur</span><span class="o">++;</span></div><div class='line' id='LC173'>		      <span class="o">}</span></div><div class='line' id='LC174'>		    <span class="o">}</span></div><div class='line' id='LC175'><br/></div><div class='line' id='LC176'>		    <span class="k">for</span><span class="o">(</span><span class="kt">int</span> <span class="n">y</span> <span class="o">=</span> <span class="mi">0</span><span class="o">;</span> <span class="n">y</span><span class="o">&lt;</span><span class="n">urlCount</span><span class="o">;</span><span class="n">y</span><span class="o">++){</span></div><div class='line' id='LC177'>		    	  <span class="n">PublicPR</span><span class="o">[</span><span class="n">y</span><span class="o">]</span> <span class="o">=</span> <span class="n">PublicPR</span><span class="o">[</span><span class="n">y</span><span class="o">]</span> <span class="o">+</span> <span class="n">dangPerPage</span><span class="o">;</span><span class="c1">//apply dangling		    	</span></div><div class='line' id='LC178'>		    <span class="o">}</span></div><div class='line' id='LC179'><br/></div><div class='line' id='LC180'>		    <span class="n">MPI</span><span class="o">.</span><span class="na">COMM_WORLD</span><span class="o">.</span><span class="na">Barrier</span><span class="o">();</span></div><div class='line' id='LC181'>		    <span class="k">if</span><span class="o">(</span><span class="n">myself</span> <span class="o">==</span> <span class="mi">0</span><span class="o">)</span> <span class="o">{</span> <span class="n">System</span><span class="o">.</span><span class="na">out</span><span class="o">.</span><span class="na">println</span><span class="o">(</span><span class="s">&quot;Allreduce COMPLETE&quot;</span><span class="o">);}</span></div><div class='line' id='LC182'><br/></div><div class='line' id='LC183'>		    <span class="n">Double</span> <span class="n">totalPR</span> <span class="o">=</span> <span class="mf">0.0</span><span class="o">;</span><span class="c1">//For verification</span></div><div class='line' id='LC184'>		    <span class="k">for</span><span class="o">(</span><span class="kt">int</span> <span class="n">z</span> <span class="o">=</span> <span class="mi">0</span><span class="o">;</span> <span class="n">z</span><span class="o">&lt;</span><span class="n">urlCount</span><span class="o">;</span><span class="n">z</span><span class="o">++){</span><span class="c1">//apply damping</span></div><div class='line' id='LC185'>		      <span class="n">PublicPR</span><span class="o">[</span><span class="n">z</span><span class="o">]</span> <span class="o">=</span> <span class="n">PublicPR</span><span class="o">[</span><span class="n">z</span><span class="o">]</span> <span class="o">*</span> <span class="n">damping</span> <span class="o">+</span> <span class="o">((</span><span class="mi">1</span> <span class="o">-</span> <span class="n">damping</span><span class="o">)/</span><span class="n">urlCount</span><span class="o">);</span></div><div class='line' id='LC186'>	    	  <span class="n">System</span><span class="o">.</span><span class="na">out</span><span class="o">.</span><span class="na">println</span><span class="o">(</span><span class="s">&quot;AFTER DANG+DAMP node: &quot;</span> <span class="o">+</span> <span class="n">z</span> <span class="o">+</span> <span class="s">&quot; :PubPr[]is: &quot;</span> <span class="o">+</span> <span class="n">PublicPR</span><span class="o">[</span><span class="n">z</span><span class="o">]);</span></div><div class='line' id='LC187'>	    	  <span class="n">totalPR</span> <span class="o">+=</span> <span class="n">PublicPR</span><span class="o">[</span><span class="n">z</span><span class="o">];</span></div><div class='line' id='LC188'>		    <span class="o">}</span></div><div class='line' id='LC189'>		    <span class="n">System</span><span class="o">.</span><span class="na">out</span><span class="o">.</span><span class="na">println</span><span class="o">(</span><span class="s">&quot;TotalPR value (SUM) is: &quot;</span> <span class="o">+</span> <span class="n">totalPR</span><span class="o">);</span></div><div class='line' id='LC190'>		  <span class="o">}</span></div><div class='line' id='LC191'><span class="o">}</span></div></pre></div>
              
            
          </td>
        </tr>
      </table>
    
  </div>


          </div>
        </div>
      </div>
    </div>
  

  </div>


<div class="frame frame-loading" style="display:none;">
  <img src="https://d3nwyuy0nl342s.cloudfront.net/images/modules/ajax/big_spinner_336699.gif" height="32" width="32">
</div>

    </div>
  
      
    </div>

    <div id="footer" class="clearfix">
      <div class="site">
        <div class="sponsor">
          <a href="http://www.rackspace.com" class="logo">
            <img alt="Dedicated Server" height="36" src="https://d3nwyuy0nl342s.cloudfront.net/images/modules/footer/rackspace_logo.png?v2" width="38" />
          </a>
          Powered by the <a href="http://www.rackspace.com ">Dedicated
          Servers</a> and<br/> <a href="http://www.rackspacecloud.com">Cloud
          Computing</a> of Rackspace Hosting<span>&reg;</span>
        </div>

        <ul class="links">
          <li class="blog"><a href="https://github.com/blog">Blog</a></li>
          <li><a href="/login/multipass?to=http%3A%2F%2Fsupport.github.com">Support</a></li>
          <li><a href="https://github.com/training">Training</a></li>
          <li><a href="http://jobs.github.com">Job Board</a></li>
          <li><a href="http://shop.github.com">Shop</a></li>
          <li><a href="https://github.com/contact">Contact</a></li>
          <li><a href="http://develop.github.com">API</a></li>
          <li><a href="http://status.github.com">Status</a></li>
        </ul>
        <ul class="sosueme">
          <li class="main">&copy; 2011 <span id="_rrt" title="0.90103s from fe3.rs.github.com">GitHub</span> Inc. All rights reserved.</li>
          <li><a href="/site/terms">Terms of Service</a></li>
          <li><a href="/site/privacy">Privacy</a></li>
          <li><a href="https://github.com/security">Security</a></li>
        </ul>
      </div>
    </div><!-- /#footer -->

    
      
      
        <!-- current locale:  -->
        <div class="locales instapaper_ignore readability-footer">
          <div class="site">

            <ul class="choices clearfix limited-locales">
              <li><span class="current">English</span></li>
              
                  <li><a rel="nofollow" href="?locale=de">Deutsch</a></li>
              
                  <li><a rel="nofollow" href="?locale=fr">Français</a></li>
              
                  <li><a rel="nofollow" href="?locale=ja">日本語</a></li>
              
                  <li><a rel="nofollow" href="?locale=pt-BR">Português (BR)</a></li>
              
                  <li><a rel="nofollow" href="?locale=ru">Русский</a></li>
              
                  <li><a rel="nofollow" href="?locale=zh">中文</a></li>
              
              <li class="all"><a href="#" class="minibutton btn-forward js-all-locales"><span><span class="icon"></span>See all available languages</span></a></li>
            </ul>

            <div class="all-locales clearfix">
              <h3>Your current locale selection: <strong>English</strong>. Choose another?</h3>
              
              
                <ul class="choices">
                  
                      <li><a rel="nofollow" href="?locale=en">English</a></li>
                  
                      <li><a rel="nofollow" href="?locale=af">Afrikaans</a></li>
                  
                      <li><a rel="nofollow" href="?locale=ca">Català</a></li>
                  
                      <li><a rel="nofollow" href="?locale=cs">Čeština</a></li>
                  
                      <li><a rel="nofollow" href="?locale=de">Deutsch</a></li>
                  
                </ul>
              
                <ul class="choices">
                  
                      <li><a rel="nofollow" href="?locale=es">Español</a></li>
                  
                      <li><a rel="nofollow" href="?locale=fr">Français</a></li>
                  
                      <li><a rel="nofollow" href="?locale=hr">Hrvatski</a></li>
                  
                      <li><a rel="nofollow" href="?locale=hu">Magyar</a></li>
                  
                      <li><a rel="nofollow" href="?locale=id">Indonesia</a></li>
                  
                </ul>
              
                <ul class="choices">
                  
                      <li><a rel="nofollow" href="?locale=it">Italiano</a></li>
                  
                      <li><a rel="nofollow" href="?locale=ja">日本語</a></li>
                  
                      <li><a rel="nofollow" href="?locale=nl">Nederlands</a></li>
                  
                      <li><a rel="nofollow" href="?locale=no">Norsk</a></li>
                  
                      <li><a rel="nofollow" href="?locale=pl">Polski</a></li>
                  
                </ul>
              
                <ul class="choices">
                  
                      <li><a rel="nofollow" href="?locale=pt-BR">Português (BR)</a></li>
                  
                      <li><a rel="nofollow" href="?locale=ru">Русский</a></li>
                  
                      <li><a rel="nofollow" href="?locale=sr">Српски</a></li>
                  
                      <li><a rel="nofollow" href="?locale=sv">Svenska</a></li>
                  
                      <li><a rel="nofollow" href="?locale=zh">中文</a></li>
                  
                </ul>
              
            </div>

          </div>
          <div class="fade"></div>
        </div>
      
    

    <script>window._auth_token = "80be9f430bb26fc4b7bf36ff77aaada31998d567"</script>
    

<div id="keyboard_shortcuts_pane" class="instapaper_ignore readability-extra" style="display:none">
  <h2>Keyboard Shortcuts <small><a href="#" class="js-see-all-keyboard-shortcuts">(see all)</a></small></h2>

  <div class="columns threecols">
    <div class="column first">
      <h3>Site wide shortcuts</h3>
      <dl class="keyboard-mappings">
        <dt>s</dt>
        <dd>Focus site search</dd>
      </dl>
      <dl class="keyboard-mappings">
        <dt>?</dt>
        <dd>Bring up this help dialog</dd>
      </dl>
    </div><!-- /.column.first -->

    <div class="column middle" style='display:none'>
      <h3>Commit list</h3>
      <dl class="keyboard-mappings">
        <dt>j</dt>
        <dd>Move selected down</dd>
      </dl>
      <dl class="keyboard-mappings">
        <dt>k</dt>
        <dd>Move selected up</dd>
      </dl>
      <dl class="keyboard-mappings">
        <dt>t</dt>
        <dd>Open tree</dd>
      </dl>
      <dl class="keyboard-mappings">
        <dt>p</dt>
        <dd>Open parent</dd>
      </dl>
      <dl class="keyboard-mappings">
        <dt>c <em>or</em> o <em>or</em> enter</dt>
        <dd>Open commit</dd>
      </dl>
    </div><!-- /.column.first -->

    <div class="column last" style='display:none'>
      <h3>Pull request list</h3>
      <dl class="keyboard-mappings">
        <dt>j</dt>
        <dd>Move selected down</dd>
      </dl>
      <dl class="keyboard-mappings">
        <dt>k</dt>
        <dd>Move selected up</dd>
      </dl>
      <dl class="keyboard-mappings">
        <dt>o <em>or</em> enter</dt>
        <dd>Open issue</dd>
      </dl>
    </div><!-- /.columns.last -->

  </div><!-- /.columns.equacols -->

  <div style='display:none'>
    <div class="rule"></div>

    <h3>Issues</h3>

    <div class="columns threecols">
      <div class="column first">
        <dl class="keyboard-mappings">
          <dt>j</dt>
          <dd>Move selected down</dd>
        </dl>
        <dl class="keyboard-mappings">
          <dt>k</dt>
          <dd>Move selected up</dd>
        </dl>
        <dl class="keyboard-mappings">
          <dt>x</dt>
          <dd>Toggle select target</dd>
        </dl>
        <dl class="keyboard-mappings">
          <dt>o <em>or</em> enter</dt>
          <dd>Open issue</dd>
        </dl>
      </div><!-- /.column.first -->
      <div class="column middle">
        <dl class="keyboard-mappings">
          <dt>I</dt>
          <dd>Mark selected as read</dd>
        </dl>
        <dl class="keyboard-mappings">
          <dt>U</dt>
          <dd>Mark selected as unread</dd>
        </dl>
        <dl class="keyboard-mappings">
          <dt>e</dt>
          <dd>Close selected</dd>
        </dl>
        <dl class="keyboard-mappings">
          <dt>y</dt>
          <dd>Remove selected from view</dd>
        </dl>
      </div><!-- /.column.middle -->
      <div class="column last">
        <dl class="keyboard-mappings">
          <dt>c</dt>
          <dd>Create issue</dd>
        </dl>
        <dl class="keyboard-mappings">
          <dt>l</dt>
          <dd>Create label</dd>
        </dl>
        <dl class="keyboard-mappings">
          <dt>i</dt>
          <dd>Back to inbox</dd>
        </dl>
        <dl class="keyboard-mappings">
          <dt>u</dt>
          <dd>Back to issues</dd>
        </dl>
        <dl class="keyboard-mappings">
          <dt>/</dt>
          <dd>Focus issues search</dd>
        </dl>
      </div>
    </div>
  </div>

  <div style='display:none'>
    <div class="rule"></div>

    <h3>Network Graph</h3>
    <div class="columns equacols">
      <div class="column first">
        <dl class="keyboard-mappings">
          <dt><span class="badmono">←</span> <em>or</em> h</dt>
          <dd>Scroll left</dd>
        </dl>
        <dl class="keyboard-mappings">
          <dt><span class="badmono">→</span> <em>or</em> l</dt>
          <dd>Scroll right</dd>
        </dl>
        <dl class="keyboard-mappings">
          <dt><span class="badmono">↑</span> <em>or</em> k</dt>
          <dd>Scroll up</dd>
        </dl>
        <dl class="keyboard-mappings">
          <dt><span class="badmono">↓</span> <em>or</em> j</dt>
          <dd>Scroll down</dd>
        </dl>
        <dl class="keyboard-mappings">
          <dt>t</dt>
          <dd>Toggle visibility of head labels</dd>
        </dl>
      </div><!-- /.column.first -->
      <div class="column last">
        <dl class="keyboard-mappings">
          <dt>shift <span class="badmono">←</span> <em>or</em> shift h</dt>
          <dd>Scroll all the way left</dd>
        </dl>
        <dl class="keyboard-mappings">
          <dt>shift <span class="badmono">→</span> <em>or</em> shift l</dt>
          <dd>Scroll all the way right</dd>
        </dl>
        <dl class="keyboard-mappings">
          <dt>shift <span class="badmono">↑</span> <em>or</em> shift k</dt>
          <dd>Scroll all the way up</dd>
        </dl>
        <dl class="keyboard-mappings">
          <dt>shift <span class="badmono">↓</span> <em>or</em> shift j</dt>
          <dd>Scroll all the way down</dd>
        </dl>
      </div><!-- /.column.last -->
    </div>
  </div>

  <div >
    <div class="rule"></div>

    <h3>Source Code Browsing</h3>
    <div class="columns threecols">
      <div class="column first">
        <dl class="keyboard-mappings">
          <dt>t</dt>
          <dd>Activates the file finder</dd>
        </dl>
        <dl class="keyboard-mappings">
          <dt>l</dt>
          <dd>Jump to line</dd>
        </dl>
      </div>
    </div>
  </div>

</div>
    

    <!--[if IE 8]>
    <script type="text/javascript" charset="utf-8">
      $(document.body).addClass("ie8")
    </script>
    <![endif]-->

    <!--[if IE 7]>
    <script type="text/javascript" charset="utf-8">
      $(document.body).addClass("ie7")
    </script>
    <![endif]-->

    
    <script type='text/javascript'></script>
    
  </body>
</html>


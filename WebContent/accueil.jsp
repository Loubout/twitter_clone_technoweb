<!DOCTYPE html>

<html>

<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="./main.css" type="text/css">
    <link rel="icon" type="image/png" href="img/favicon.ico" />
    <title>Home | Tweetzel</title>
</head>

<body>
    <div class="bg"><img src="img/background.jpg">
    </div>

    <!--Bandeau -->
    <header>
        <div id="titre">
            <img src="img/logo.gif" />
            <h1> Mon Espace </h1>
            <i> Les meilleurs bretzels du monde magique</i>
        </div>
        <div id="logout">
            <form method="get" id="deco" action="javascript:void(0);" onSubmit="return deconnecte();">
                <input type="submit" value="Partir (snif snif)" />
            </form>
        </div>
        <div id="photo">
            <!--ajax -->
        </div>
        <div id="nom" onMouseOver="showHide_a();">
        </div>
        <div id="desc" onMouseOut="showHide_b();">
        </div>
        <div id="logout">
    </header>


    <!-- Commentaires a écrire -->
    <section>
        <div id="commentaires">
            <article>
                <form method="get" action='javascript:void(0);' id="tweetForm" onSubmit="return verifTweet();">
                    <fieldset>
                        <textarea id="tweetField" name="txt" maxlength="140" rows="5" cols="50" placeholder="J'aime les licoooornes !" onpaste="countChar();" onkeydown="countChar();" onkeyup="countChar();"></textarea>
                        <span style='background-color: #63d5ff; '> <input type="radio" id="radio01" name="color" value="#63d5ff" checked="checked">  </input></span>
                        <span style='background-color: #FF0000; '> <input type="radio" id="radio02" name="color" value="red" >   </input></span>
                        <span style='background-color: #FFFF00; '> <input type="radio" id="radio03" name="color" value="yellow" >   </input></span>
                        <span style='background-color: #01DF01; '> <input type="radio" id="radio04" name="color" value="lime" >   </input></span>
                        <span style='background-color: #F5ECCE; '> <input type="radio" id="radio05" name="color" value="white" >   </input></span>
                        </br>
                        <span><input type="submit" value="Ecrire un message plein de bonheur"/></span>
                        <span id="compteur"> 0 / 140 </span>
                    </fieldset>
                </form>
            </article>
        </div>
    </section>



    <!-- Le bandeau ou y'aura les amis : ajax-->
    <section id="friends">

    </section>



    <!--Le bandeau ou yaura les tweet : ajax -->
    <section id="wrapper">
        <form method="get" id="tweetSearchForm" action="javascript:void(0);" onsubmit="search();" />
        <input type="text" id="searchQuery" name="query" placeholder="Entrez votre recherche et cochez pour ne voir que vos amis !" />
        <input type="checkbox" id="contacts_checkbox" name="contacts_only" />
        <input type="submit" value="LAUNCH." />
        </form>
        <hr/>
        <div class="tweetContainer" id="tweetContainer">
        </div>
    </section>




    <!--Javascript-->
    <script language="javascript" type="text/javascript" src="http://code.jquery.com/jquery-1.7.1.min.js"></script>
    <script language="javascript" type="text/javascript" src="main.js"></script>
    <script language="javascript" type="text/javascript" src="tweet.js"></script>

    <!--JSP : récupération des arguments -->
    <script>
        function init_accueil() { <%
            String id = request.getParameter("id");
            String login = request.getParameter("login");
            String key = request.getParameter("key");
            String friends = request.getParameter("friends");
            String desc = request.getParameter("desc");
            String photo = request.getParameter("photo");

            if (id != null) {
                out.println("main_accueil('" + id + "','" + login + "','" + key + "','" + friends + "','" + photo + "')");
            } else {
                out.println("main_accueil();");
            }
            out.println("search();"); %>
        }

        $(init_accueil);
    </script>

</body>

</html>
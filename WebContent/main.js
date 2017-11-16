/*TWEETZBEUL*/

/*********************************/
/*      Fonctions principales    */
/*********************************/

/* Cette fonction se lance a l'ouverture de la page index*/
function main_index() {
    nyan();

}

/* Cette fonction se lance a l'ouverture de la page accueil*/
function main_accueil(id, login, key, friends, photo) {

    /* Objet environnement */
    env = new Object();

    /*Si appel JSP ok */
    if ((id != undefined) && (login != undefined) && (key != undefined)) {

        /*Constructeurs */
        env.actif = new User(id, login);
        env.key = key;
        env.users = [];
        env.photo = photo;

        /* liste d'ami */
        var tabAmis = friends.split(",");
        getInformation(tabAmis);
        env.friends = friends;

        /*Affichage des info de l'utilisateur */
        getInformationUser(id);
    }
}

/* Erreur ajax */
function ajax_erreur(jqXHR, textStatus, thrownError) {
    alert("ERREUR AJAX :" + textStatus);
}

/*********************************/
/*      Constructeurs 		     */
/*********************************/

/*Constructeur d'utilisateur */
function User(id, login, contact) {
    this.id = id;
    this.login = login;
    this.contact = false;

    /*determine si l'user est ami avec l'utilisateur connecté*/
    if (contact != undefined) {
        this.contact = contact;
    }

    /*if (env == undefined) env = new Object();
    if (env.users == undefined) env.users = [];*/ // A REVOIR

}

/*Constructeur de commentaire */
function Commentaire(id, author, texte, date, score, photo, bgColor) {

    this.id = id;
    this.author = author; // type user
    this.texte = texte;
    this.date = date; // type date
    this.score = score; // le score de pertinence par rapport à la recherche effectuée

    /* Modification apres coup : prise en charge des tweet obsoletes */
    if (photo != undefined) {
        this.photo = photo;
    } else {
        this.photo = 'img/default_profil.jpg';
    }
    if (bgColor != undefined) {
        this.bgColor = bgColor;
    } else {
        this.bgColor = '#ffb6c1';
    }
}


/*********************************/
/*      LoginService   		     */
/*********************************/

/* appel depuis le formulaire */
function verifLogin() {
    var form = document.getElementById("loginForm");
    var login = form.elements["login"].value;
    var pwd = form.elements["pwd"].value;

    if (login.length < 2 || pwd.length < 2) {
        if ($('#err').length) {
            $('#err').show();
        } else {
            var $err = "<div id=\"err\"><img src= \"img/err.gif\"/><span> Tu t'es trompé quelque part :'( </span></div> ";
            $('body').append($err);
        }
        return false;
    }
    /* appel d'ajax*/
    connecte(login, pwd);
}

/* ajax connection*/
function connecte(login, pass) {
    console.log("Et c'est parti ma poule !");
    $.ajax({
        type: "GET",
        url: "user/login",
        data: "login=" + login + "&pwd=" + pass,
        dataType: "json",
        success: traiteReponseConnexion,
        error: ajax_erreur
    });
}

/*Reponse connection*/
function traiteReponseConnexion(json) {
    console.log("appel de connection ");
    console.log(json);
    if (json.code != undefined) {
        var err = "<div id='err'><img src='img/err.gif'/><span> Tu t'es trompé quelque part :'( </span></div> ";
        $('body').append(err);
    } else {
        window.location.href = "accueil.jsp?id=" + json.id +
            "&login=" + json.login +
            "&key=" + json.key +
            "&friends=" + json.friends +
            "&photo=" + json.photo;
    }
}



/**********************************/
/* Récuperation & affichage infos */
/**********************************/


/* appel depuis le main du jsp : Info utilisateur + affichage*/
function getInformationUser(id) {
    /*ajax, service*/
    $.ajax({
        type: "GET",
        url: "user/getInformations",
        data: "ids=" + id,
        dataType: "json",
        success: traiteReponsegetInformationsUser,
        error: ajax_erreur
    });
}

/*Reponse getInfUser*/
function traiteReponsegetInformationsUser(json) {
    for (var val in json) {
        console.log("appel de banner user");
        console.log(json);
        bannerUser(json[val].login, json[val].desc, json[val].photo);
    }
}

/* Affichage des infos sur l'accueil avec mini animation tavu*/
function bannerUser(name, desc, photo) {

    $('#nom').append(name);
    if (photo) {
        $('#photo').append("<img id=photo_u src=" + photo + "></img>");
    }
    if (desc) {
        $('#desc').append(desc);
    }
}


/*Info ami*/
function getInformation(tab) {
    /*ajax, service*/
    $.ajax({
        type: "GET",
        url: "user/getInformations",
        data: "ids=" + tab,
        dataType: "json",
        success: traiteReponsegetInformations,
        error: ajax_erreur
    });
}


/*Reponse informations*/
function traiteReponsegetInformations(json) {
    console.log("Amis de l'user :");
    console.log(json);
    bannerFriend(json);
    /* ici est stocké THE tableau */
    env.friends = json;
}

/* Informations sur les amis de l'utilisateurs */
function bannerFriend(friends) {

    /* Formulaire d'ajout*/
    var s1 = '<form id="addForm" method="get" id="addFriend"  action="javascript:void(0);" onSubmit="return verifaddFriend();">';
    var s2 = '<input type="text" name="logFriend"  placeholder="Nom de ton nouvel ami"/>';
    var s3 = '<input type="submit" value="Rajouter en ami !"/></form>';
    $('#friends').append(s1 + s2 + s3);

    /* Info sur amis + formulaire de Suppression */
    for (var i in friends) {
        if (i != 404) {
            var s1 = '<div class="ami"><div class="nom_ami">' + friends[i].login + '</div><div class="photo_ami"><img src="' + friends[i].photo + '"/></div>';
            var s2 = '<form method="get" id="suppFriend"  action="javascript:void(0);" onSubmit="return suppFriend(' + i + ');">';
            var s3 = '<input type="submit" value="Jeter "/></form>';
            $('#friends').append(s1 + s2 + s3);
        }
    }

}



/*********************************/
/*      LogoutService  		     */
/*********************************/

/*Deconnection*/
function deconnecte() {
    $.ajax({
        type: "GET",
        url: "user/logout",
        data: "login=" + env.actif.login,
        dataType: "json",
        success: traiteReponseDeconnexion,
        error: ajax_erreur
    });
}

/*Reponse deconnection*/
function traiteReponseDeconnexion(json) {
    window.location.href = "index.html";
}




/*********************************/
/*      InscriptionService	     */
/*********************************/


/* Appel depuis le formulaire */
function verifIns() {
    var form = document.getElementById("inscriptForm2");
    var login = form.elements["login"].value;
    var pwd = form.elements["pwd"].value;
    var fn = form.elements["firstname"].value;
    var ln = form.elements["lastname"].value;
    var tof = form.elements["photo"].value;
    var desc = form.elements["desc"].value.replace("'", "''");

    if (login.length < 2 || pwd.length < 2 || fn.length < 2) {
        if ($("#err2").length) {
            $('#err2').show();

        } else {
            var err2 = "<div id=\"err2\"><img src= \"img/err.gif\"/><span>Tu a oublié quelque chose !</span></div> ";
            $('body').append(err2);
        }
        return false;
    }
    /*appel d'ajax */
    inscription(login, pwd, fn, ln, tof, desc);

}

/*Ajax inscription*/
function inscription(login, pwd, firstname, lastname, photo, desc) {
    /* On STocke directement les variables login et pwd ! Si ca marche pas pas grave elle seront écrasées a la prochaine tentative */
    User(login, pwd);
    console.log("AJAX FTW");
    $.ajax({
        type: "GET",
        url: "user/create",
        data: "login=" + login + "&pwd=" + pwd + "&firstname=" + firstname + "&lastname=" + lastname + "&photo=" + photo + "&desc=" + desc,
        dataType: "json",
        success: traiteReponseInscription,
        error: ajax_erreur
    });
}

/* Réponse Inscription */
function traiteReponseInscription(json) {
    console.log(json);

    if (json.code != undefined) {
        var $err = "<div id=\"err\"><img src= \"img/err.gif\"/><span> Tu t'es trompé quelque part :'( </span></div> ";
        $('body').append($err);
    } else {
        /* L'inscription a réussi ! */
        //var $err = "<div id=\"err\"><span> Bienvenue parmi nous, on va bien s'amuser :D </span></div> ";
        $('body').append($err);
        showHide();
        $('#inscriptionStatus').html("Tu es désormais inscrit(e) <br> Bienvenue parmi nous !! <br> Tu peux dès maintenant te connecter avec tes identifiants à l'aide du formulaire ci-dessus").show();
    }
}



/*********************************/
/*      	TweetService	     */
/*********************************/

/*Vérifie les champs : tweet*/ //PAS AU POINT ENCORE
function verifTweet() {
    var form = document.getElementById("tweetForm");
    var com = form.elements["txt"].value;
    var color = form.elements["color"].value;

    if (com.length < 2) {
        var err3 = "<div id=\"err3\"><img src= \"img/err.gif\"/><span> En manque d'inspiration ? Prends donc un bretzel !</span></div> ";
        $('body').append(err3);
        return false;
    } else {
        sendTweet(com, color);
    }
}

/* Modifier l'attribut contact selon si le user correspondant fait partie des 'amis' des l'utilisateur  pour la bouton d'ajout d'ami notamment */
User.prototype.modifStatus = function () {
    this.contact = !this.contact;
}




/*********************************/
/*  	  AddFriends Service     */
/*********************************/


/* Appel depuis le formulaire */
function verifaddFriend() {
    var form = document.getElementById("addForm");
    var login = form.elements["logFriend"].value;
    /* ajax*/
    addFriend(login);
}

/* Ajouter ami*/
function addFriend(friend) {
    console.log("AJAX FTW ADDFRIEND DEMANDE");

    /*ajax, service*/
    $.ajax({
        type: "GET",
        url: "user/addFriend",
        data: "loginFriend=" + friend + "&key=" + env.key,
        dataType: "json",
        success: function (data, friend) {
            traiteReponseaddFriend(data, friend)
        },
        error: ajax_erreur
    });
}

/* Reponse addFriend */
function traiteReponseaddFriend(json, loginFriend) {
    console.log("AddFriends :");
    console.log(json);
    if (json.code != undefined) {
        alert("Cet utilisateur ne fait pas partie du monde magique :( ");
    } else {
        //alert("Ta demande a bien ete enregistre, tu va avoir un nouvel ami :D ");
        console.lo g("addFriend bien enregistré");
        $('.friendButton').each(function () {
            if (this.attr('onclick') == 'addFriend("' + loginFriend + '");') {
                this.attr('onclick', "");
                this.html("C'est fait !!");
            }
        });
    }
}

/*********************************/
/*  	  SuppFriends Service    */
/*********************************/


/* Supprimer ami*/
function suppFriend(friend) {
    console.log("AJAX FTW SUPPFRIEND DEMANDE");

    /*ajax, service*/
    $.ajax({
        type: "GET",
        url: "user/removeFriend",
        data: "idFriend=" + friend + "&key=" + env.key,
        dataType: "json",
        success: function (data, friend) {
            traiteReponsesuppFriend(data, friend);
        },
        error: ajax_erreur
    });
}

/*Reponse AsuppFriend*/
function traiteReponsesuppFriend(json, idFriend) {
    if (json.code != undefined) {
        alert("pb ça n'a pas marché");
    } else {
        console.log("supp friend enregistré !");
        $('.friendButton').each(function () {
            if (this.attr('onclick') == 'suppFriend("' + idFriend + '");') {
                this.attr('onclick', "");
                this.html("C'est fait !");
            }
        });
    }

}

/*********************************/
/*  	  Design et animations   */
/*********************************/

/* Dat Unicorne*/
var ponySpeed = 0.5; // vitesse du poney

function nyan() {
    var pony = $("#nyan");
    pos = -50;
    timer = setInterval(function () {
        movePoney(pony)
    }, 40);

    pony.hover(function (e) {
        if ($('#unicornTooltip').length == 0) {
            var title = $(this).attr('title');
            $(this).removeAttr('title');
            $('body').append($('<p id="unicornTooltip"></p>').html(title));
        } else {
            $("#unicornTooltip").show();
        }
        clearInterval(timer);
    }, function () {
        $("#unicornTooltip").hide();
        timer = setInterval(function () {
            movePoney(pony)
        }, 40);
    }).mousemove(function (e) {
        var mousex = e.pageX + 20; //Get X coordinates
        var mousey = e.pageY + 10; //Get Y coordinates
        //console.log(mousex + " " + mousey);
        $('#unicornTooltip').css({
            top: mousey,
            left: mousex
        })
    });

    $("#nyan").on(
        "click",
        function (e) {
            ponySpeed += 0.5;
        });
}

function movePone y(pony) {
    if (pos >= 100) pos = -50;

    pony.css('left', (pos) + "%");
    pos += ponySpeed;
}

/* affiche ou cache le formulaire d'inscription ET les messages d'erreurs (héhéhé) */
function showHide() {
    var id = "inscriptForm";
    if (document.getElementById(id).style.visibility == "hidden") {
        document.getElementById(id).style.visibility = "visible";
    } else {
        document.getElementById(id).style.visibility = "hidden";
        if ($('#err').length) {
            $('#err').hide();
        }
        if ($('#err2').length) {
            $('#err2').hide();
        }
    }
}

/*Comte le nombre de lettre du formulaire*/
function countChar() {
    var form = document.getElementById("tweetForm");
    var com = $('#tweetField').val();
    var cpt = document.getElementById("compteur");
    cpt.innerHTML = "" + (com.length) + " / 140";
}

/* Animation a l'inscription c'est mal codé j'avou*/
function showHide_a() {
    var div = document.getElementById("desc");
    var div2 = document.getElementById("nom");

    if (div.style.display == "none") {

        div.style.display = "block";
        div2.style.display = "none";
    } else {
        div.style.display = "none"      ;     
    }
}

function showHide_b() {
    var div2 = document.getElementById("desc");
    var div = document.getElementById("nom");

    if (div.style.display == "none") {

        div.style.display = "block";
        div2.style.display = "none";
    } else {
        div.style.display = "none";

    }
}
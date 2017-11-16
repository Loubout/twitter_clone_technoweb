/*TWEETZBEUL*/

/*********************************/
/*          Envoi de Tweet       */
/*********************************/

/* fonction qui insère un nouveau tweet dans la mongoDB en ajax */
function sendTweet(text, color) {
    var key = env.key;

    $.ajax({
        type: "GET",
        url: "comment/addComment",
        data: "key=" + key + "&txt=" + text + "&color=" + color,
        dataType: "json",
        success: function (data) {
            traiterReponseTweet(data, text, color);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert(jqXHR + " " + textStatus + " " + errorThrown);
        }
    });
}

/* traite la réponse après l'ajout d'un tweet pour l'afficher sur la page */
function traiterReponseTweet(data, text, color) {
    console.log('traiterReponseTweet ');
    if (data.erreur == undefined) {
        $('#tweetField').val(''); // vider le textarea qui contenait le texte du tweet
        countChar(); // pour remetter le compteur à zéro
        var com = new Commentaire(undefined, env.actif, text, new Date(), 0, env.photo, color);
        $('#tweetContainer').prepend($(com.getHTML()).fadeIn(1000));
    }
    console.log('traiterReponseTweet ok');
}

/* Renvoie la div html contenant le commentaire */
Commentaire.prototype.getHTML = function () {

    var s = "<div class='tweet'  style='background-color:" + this.bgColor + ";' id=" + this.id + ">"
    s += "<div class='authorInfo'>"
    s += "<img src='" + this.photo + "'>"
    s += "<span class='name'>" + this.author.login + "</span>"
    s += "<span class='date'>" + this.date.toDateString() + "</span>"
    if (this.author.id == env.actif.id) {
        s += ''
    } else if (this.author.contact == true) {
        var clickEventAttr = "onclick='suppFriend(\"" + this.author.id + "\");'";
        s += "<button type='button' class='friendButton' " + clickEventAttr + ">  Remove friend </button>";
    } else {
        var clickEventAttr = "onclick='addFriend(\"" + this.author.login + "\");'";
        s += "<button type='button' class='friendButton' " + clickEventAttr + "> Add as a friend </button>";
    }
    s += "</div>"
    s += "<p class='content'>" + this.texte + "</p>"
    s += "</div>"

    return s;

}


/*********************************/
/*      Recherche de Tweet       */
/*********************************/

/*Constructeur de recherche de commentaires */
function RechercheCommentaires(results, query, contacts_only, author, date) {
    this.results = results; // tableau de tweet -> résultat de la recherche
    this.query = query; // la recherche dont est issu le resultat
    this.contacts_only = contacts_only;
    this.author = author; // objet user auteur de la recherche
    this.date = new Date();
    if (date != undefined) {
        this.date = date;
    }
}

/* fonction de recherche de tweet */
function search() {

    var key = "";
    if (env.key != undefined) {
        key = env.key;
    }
    var query = $("#searchQuery").val();

    var friends = (($("#contacts_checkbox").get(0).checked) ? 1 : 0); // le get permet de passer d'un obj jQuery à l'objet du DOM

    $.ajax({
        type: "GET",
        url: "comment/findComment",
        data: "key=" + key + "&query=" + query + "&friends=" + friends,
        dataType: "text", // pour utiliser reviver et faire les transformations néccéssaires
        success: function (data) {
            RechercheCommentaires.traiteReponseJSON(data);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert(jqXHR + " " + textStatus + " " + errorThrown);
        }
    });
}

/* injecte l'affichage des tweet dans la page web */
RechercheCommentaires.traiteReponseJSON = function (json_text) {

    console.log("Resultat recherche :");
    console.log(json_text);

    var old_users = env.users;
    env.users = []; // on va affecter env.users avec les users issus de la recherche de commentaires
    var obj = JSON.parse(json_text, RechercheCommentaires.reviver);

    if (obj.erreur == undefined) {
        if (obj.query != "") { // on va trier les résultats par score de pertinence si la query n'était pas vide
            obj.results.sort(function (a, b) {
                return parseFloat(b.score) - parseFloat(a.score)
            });
            // console.log("after sort by score" + JSON.stringify(obj.results));
        } else { // sinon on trie par ordre chronologique
            obj.results.sort(function (a, b) {
                return new Date(b.date).getTime() - new Date(a.date).getTime();
            });
            // console.log("after sort by date" + JSON.stringify(obj.results));
        }
        $("#tweetContainer").html(obj.getHTML()); // on insère les commentaires issus de la recherche

    } else {
        env.users = old_users;
    }
}

/* renvoie l'affichage de l'ensemble des résultats */
RechercheCommentaires.prototype.getHTML = function () {
    var s = "";
    for (var i = 0; i < this.results.length; i++) {
        s += this.results[i].getHTML();
    }
    return s;
}



/*********************************/
/*      Outils                   */
/*********************************/

/* fonction reviver qui est utilisé pour construire les objets javascript en parsant le json */
RechercheCommentaires.reviver = function (key, value) {

    if (key.length == 0) { // pas Fde clé => tout début ou fin du json
        var r;
        if (value.erreur == undefined || value.erreur == 0) { // dans notre cas ce sera toujour undefined si tout s'est bien passey
            r = new RechercheCommentaires(value.results, value.query, value.contacts_only, value.author, value.date);
        } else {
            r = new Object(); // objet générique dans lequel on stocke l'erreur
            r.erreur = value.erreur;
        }
        return r;
    } else if (isNumber(key) && (value.author instanceof User)) {
        var c = new Commentaire(value._id, value.author, value.text, value.date, value.score, value.photo, value.bgColor); /* COmment je récupère cette p*** de photo et couleur ?*/
        return c;
    } else if (key == "date") {
        var d = new Date(value);
        return d;
    } else if (key == "author") {
        var u;
        if (env != undefined && env.users != undefined && env.users[value.id] != undefined) {
            u = env.users[value.id];
        } else {
            /* pour le moment on stocke pas contact dans le résultat de la recherche du coup on sait pas si le tweet qu'on recuppere vient d'un ami faudrait voir ça */
            u = new User(value.id, value.login, value.contact);
        }
        return u;
    } else {
        return value;
    }
}

function isNumber(s) {
    return !isNaN(s - 0);
}
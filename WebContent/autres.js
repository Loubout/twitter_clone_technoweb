
// main.js

    function main(id, login, key){
        env = new Object();
        env.users = []
        if ( (id != undefined) && (login != undefined) && (key != undefined)){
            env.actif = new User(id, login)
            env.key = key
            gererDivConnexion();
        }
        search();
        // gerer bouton de deconnexion sur evenement click
        // (#box_friends).click(func_filtre) pour filtrer les tweet des amis
    }
    
// function enregistre en ajax  a ecrire de la même manière

function search(){
    if (env.key != undefined){
        var key = env.key
        }
    
    var query = $("#searchQuery").val();
    var friends = (($("#box_friends").get(0).checked)? 1:0); // le get permet de passer d'un obj jQuery à l'objet du DOM
    
    $.ajax({ type: "GET",
             url : "comment/findComment",
             data : "key=" + key + "&query=" + query + "&friends=" + friends,
             dataType : "text",    // pour utiliser reviver et faire les transformations néccéssaires
             success : RechercheCommentaires.traiteReponseJSON,
             error : function (jqXHR, textStatus, error????) {
                alert(jqXHR + " " + textStatus + " " + error??? );
                }
        });
}

function ajoutsup_contact(id){
    var user = env.users[id]
    var url  = "addFriend";
    if (users.contact) {
        url = "removeFriend";
    }
    
    $ajax({type :"GET",
           url : url,
           data: "key=" + env.key + "&id_friend=" + id,
           dataType : "json",
           success : function(rep) {
                traiteReponseAjoutContact(rep, id);
                };
            error : function (jqXHR, textStatus, error????) {
                alert(jqXHR + " " + textStatus + " " + error??? );
                }
        });
}
                
    

            
    
    


            
        
    
# TODO LIST

## Must-Have Improvements

1. Barema
1. Llistat provisional a directors
1. Llistat definitiu a directors
1. Cartes (Due ASAP): 
    1. Docent escola pública
        1. Centre
            1. Català X
        1. Empresa
            1. Català X
            1. Castellà X
            1. Anglès X
            1. Francès
    1. Docent escola privada/concertada (No han d'incloure cap referència a les assegurances)
        1. Centre
            1. Català X
        1. Empresa
            1. Català X
            1. Castellà
            1. Anglès
            1. Francès
    1. Docent escola municipal (No han d'incloure cap referència a les assegurances...)
        1. Centre
            1. Català
        1. Empresa
            1. Català
            1. Castellà
            1. Anglès
            1. Francès
    1. Carta d'agraïment
        1. Català
        1. Castellà
        1. Anglès
        1. Francès
1. Baremació: Cal crear taules noves, (Due for October-November)
1. Review Llistes provisionals: (Due for October-November)
1. Review comunicat a directors de les Llistes provisionals: (Due for October-November)
1. Review Llistes definitives: (Due for October-November)

1. BAREMACIO
Cal crear taules noves

config_barem_t (places_privades: int, places_publiques: int, )
barem_t (…, privat boolean, grup boolean, dual boolean, nou: boolean, repeteix boolean, en_espera boolean, projecte: double, antiguitat double, formacio double, altres_titulacions: double, catedratic: double )
privats_t (nif)
grups_t (nif)
dual_t (nif)
nous_t (nif)
repetidors_t (nif)
altres_t (nif)

Reserves

Per tipus d'estada
10% DG
50% Dual
10% NOUS

Per tipus de sol·licitud
40% grup

Segons condicio professorat
20% interins

Cal crear una interfície nova

1. Careful, docent email in form should prevail over tables data
    1. PDF's email should be stored in estades_t
    1. Considering both (comma separated) could be an option
1. Review notificar llistes provisionals
    1. Principals must know who is requesting a educational stay
1. Review notificar llistes definitives
    1. Principals must know who got one

## Should-Have Improvements

1. Secundary tables CRUD management: estades_t, seguiment_t, candidats_t.
1. More email options to admesos, candidats, col·lectius, singles...
1. Settings menu: nom cap, nom funcionari, nom Departament d'Educació, adreça...
1. Help menu
1. Open log

## Could-Have Improvements

1. Look for incons on secundary views
1. Enviar SMS a l'interessat inici/final estada, reclamant documentació.
    1. https://code.google.com/archive/p/jsmpp/
1. Checkout RxKotlin

## Remember

1. Generate exe file
    1. https://intellij-support.jetbrains.com/hc/en-us/community/posts/206872335-How-to-create-executable-JAR-using-Intellij-
    1. Remember: META-INF/MANIFEST.MF to resources
    1. MANIFEST.MF must enumerate all project jar's
![Thumb Up](./thumb_up.jpg)

###### Last edit 14/03/2019
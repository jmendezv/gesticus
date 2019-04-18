# TODO LIST

## Must-Have Improvements
1. Quan es dona de baixa a algu que no esta a la bd no cal enviar email
1. Visites: visites_t, VisitesEditorView add 'Generate report' to pdf
1. Baremació
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
1. Be able to send email to actual estada

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
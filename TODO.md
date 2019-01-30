# TODO LIST

## Must-Have Improvements

1. Eventually will be necessary to undo an 'estada'.
    1. Caldra tornar a informar les institucions quan hi hagi:
        1. Canvi d'empresa
        1. Canvi de dates d'inici/final
    1. Si es tracta d'un altre canvi només cal fer un update d'estada
1. Estadístiques:
    1. Per SSTT
    1. Per Centre Top ten
    1. Per destinació: DD, IN...
    1. Per familia/especialitat
    1. Per Acabades/Cancel·lades
1. Replace Alert for Notify everywhere
1. Review AdmesosEditorView: Falta baixa
1. Method checkStats should present a summary at the very end
1. Option to send reminder recordatori a tots els admesos que no han fet estada encara.
1. Option to send reminder recordatori a tots els admesos que no han acabat però no han documentat encara.
1. When selecting 'Cerca estada' Sr./sra. seems to be missing
1. Careful, docent email in form should prevail over tables data.
    1. PDF's email should be stored in estades_t
    1. Considering both (comma separated) could be an option
1. More email options to admesos, candidats, col·lectius, singles...
1. Look for incons on secundary views
1. Be able to send email to actual estada
1. Secundary tables CRUD management: estades_t, seguiment_t, sstt_t, candidats_t, admesos_t.
1. It may not read a estada tipus A :( -> Obrir estada B i Obrir estada A
1. Add versatile find function to find estades by virtualy any field: codi_estada, nif_docent, nom_docent... It would yield a tableview to choose from
1. Implement estades A type
1. Generate exe file

## Should-Have Improvements

1. Data validation beyond null/empty: movile, date, email...
1. Settings menu: nom cap, nom funcionari, nom Departament d'Educació, adreça...
1. Help menu

## Could-Have Improvements

1. The form itself has no type A/B? It should, in order to facilitate computarize management.
1. Enviar SMS a l'interessat inici/final estada, reclamant documentació.
    1. https://code.google.com/archive/p/jsmpp/
1. Graphs/Reports
1. Checkout RxKotlin

![Thumb Up](./thumb_up.jpg)

###### Last edit 02/01/2019
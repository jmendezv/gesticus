# TODO LIST

## Must-Have Improvements

1. Búsqueda per nom amb combo amb autocomplete
1. Eventually will be necessary to modify an 'estada'
    1. Caldra tornar a informar les institucions quan hi hagi:
        1. Canvi d'empresa -> notify all but SSTT
        1. Canvi de dates d'inici/final -> notify all
    1. Si es tracta d'un altre canvi només cal fer un update d'estada
1. Careful, docent email in form should prevail over tables data
    1. PDF's email should be stored in estades_t
    1. Considering both (comma separated) could be an option
1. Be able to send email to actual estada
1. Add versatile find function to find estades by virtualy any field: codi_estada, nif_docent, nom_docent... It would yield a tableview to choose from

## Should-Have Improvements

1. Secundary tables CRUD management: estades_t, seguiment_t, sstt_t, candidats_t, admesos_t.
1. More email options to admesos, candidats, col·lectius, singles...
1. Implement estades A type
1. It may not read a estada tipus A :( -> Obrir estada B i Obrir estada A
1. Data validation beyond null/empty: movile, date, email...
1. Settings menu: nom cap, nom funcionari, nom Departament d'Educació, adreça...
1. Help menu
1. Open log

## Could-Have Improvements

1. Look for incons on secundary views
1. The form itself has no type A/B? It should, in order to facilitate computarize management.
1. Enviar SMS a l'interessat inici/final estada, reclamant documentació.
    1. https://code.google.com/archive/p/jsmpp/
1. Graphs/Reports
1. Checkout RxKotlin

## Remember

1. Generate exe file
    1. https://intellij-support.jetbrains.com/hc/en-us/community/posts/206872335-How-to-create-executable-JAR-using-Intellij-
    1. Remember: META-INF/MANIFEST.MF to resources
    1. MANIFEST.MF must enumerate all project jar's
![Thumb Up](./thumb_up.jpg)

###### Last edit 02/01/2019
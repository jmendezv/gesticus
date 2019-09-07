# TODO LIST

## Must-Have Improvements

1. I must be able to select the year I'm working with Gèsticus from a combo or a CustomMenuItem or RadioMenuItem
1. Careful, docent email in form should prevail over tables data
    1. PDF's email should be stored in estades_t
    1. Considering both (comma separated) could be an option
1. Estades id's now have 000EXX0600/2099-2099 format
1. Baremació: Cal crear taules noves, (Due for October-November)

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

1. Review Llistes provisionals: (Due for October-November)
1. Review Llistes definitives: (Due for October-November)
1. Llistat provisional a directors (Due for October-November)
1. Llistat definitiu a directors (Due for October-November)

## Should-Have Improvements

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
        1. Redo CSV Generation of stays:  X ?
           1. File activitats_2018.csv
           CODI_ACTIVITAT
           CODI_ANY
           CODI_BLOC
           CODI_CERTIFICAT
           CODI_CRP
           CODI_DT
           CODI_ENTITAT
           CODI_INSTITUCIO
           CODI_MATERIA
           CODI_MATERIA2
           CODI_MATERIA3
           CODI_MATERIA4
           CODI_MATRICULACIO
           CODI_MODALITAT
           CODI_MODALITAT_GESTIO
           CODI_MODEL_CERTIFICAT
           CODI_MUNICIPI
           CODI_NIVELL
           CODI_PROCES_INSCRIPCIO
           CODI_PROGRAMA
           CODI_PUBLICACIO
           CODI_STATUS
           CODI_TIPUS_ACTIVITAT
           CODI_TIPUS_SEMINARI
           CODI_VALIDACIO_CENTRE	
           DATA_BLOQ_ECO
           DATA_BLOQUEIG_RESPONSABLE
           DATA_FI_ASSIGNACIO
           DATA_FI_MATRICULACIO
           DATA_FI_PRIORITZACIO
           DATA_FINAL
           DATA_INICI
           DATA_INICI_ASSIGNACIO
           DATA_INICI_MATRICULACIO
           DATA_PROVISIONAL
           DATA_RESOLUCIO
           DATA_TRASPAS_ODISSEA
           IND_ALTRES_REQUISITS
           IND_INSCRIPCIO
           IND_RESTRICCIO_CENTRE
           IND_SENSE_COST
           IND_TIPUS_SEGUIMENT
           MATRICULA
           MAXIM_NOMBRE_PRIORITZACIONS
           NOM_ACTIVITAT
           NOM_CONTINGUTS
           NOM_DESCRIPCIO
           NOM_DESTINATARIS
           NOM_EMAIL_INFO
           NOM_OBJECTIUS
           NOM_OBSERVACION
           NOM_OBSERVACIONS
           NOM_PREGUNTA
           NOM_RESPONSABLE
           NOM_TITOL
           NUM_DESPESES_DESPLACAMENT
           NUM_DESPESES_DOCENCIA
           NUM_DESPESES_GENERALS
           NUM_DIES_MARGE_FINAL
           NUM_DIES_MARGE_INICIAL
           NUM_HORA_FINAL
           NUM_HORA_INICI
           NUM_HORES_ALTRES_REQUISITS
           NUM_HORES_FORMADOR
           NUM_HORES_PREVISTES
           NUM_ORDRE
           NUM_PLACES_PREVISTES
           NUM_SESSIONS
           OBSERVACIONS_LLOC
           PLA_AVALUACIO
           REQUISITS_CERTIFICACIO
           USER_BLOQ_ECO
           USERNAME_BLOQUEIG_RESPONSABLE
           1. File alumnes_2018.csv
           CODI_ACTIVITAT
           CODI_ANY
           CODI_ASSIGNACIO
           CODI_CENTRE_TREBALL
           CODI_MOTIU_BAIXA
           CODI_PERSONA
           CODI_PRIORITAT
           DATA_INSCRIPCIO
           DATA_ULTIMA_MODIFICACIO
           IND_ALTRES_REQUISITS
           IND_CERTIFICAT
           NOM_CLAU_ACCESS
           NOM_IP
           NOM_MAQUINA
           NOM_NAVEGADOR
           NOM_OBSERVACIO
           NOM_RESPOSTA
           NUM_HORES_ASSISTITS
           NUM_ORDRE_PREF
           PRIORITZACIO_DIRECCIO
           QUESTIONARI
1. Secondary tables CRUD management: estades_t, seguiment_t, candidats_t.
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
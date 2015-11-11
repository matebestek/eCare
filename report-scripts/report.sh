# 
# Copyright (c) 2013.
# University of Primorska, Andrej Marušič Institute. All rights reserved.
# 
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met: 
# 
# 1. Redistributions of source code must retain the above copyright notice, this
#    list of conditions and the following disclaimer. 
# 2. Redistributions in binary form must reproduce the above copyright notice,
#    this list of conditions and the following disclaimer in the documentation
#    and/or other materials provided with the distribution. 
# 
# Project eOskrba (http://eOskrba.si) was financed by Slovenian Research
# Agency and Ministry of Health of Republic of Slovenia.
# 
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
# "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
# LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
# A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
# OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
# SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
# LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
# DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
# THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
# (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
# OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
# 
EOSKRBA_LOG=/opt/activiti-5.4/apps/apache-tomcat-6.0.32/logs/eoskrba.log*
EOSKRBA_FILTERS_LOG=/opt/activiti-5.4/apps/apache-tomcat-6.0.32/logs/eoskrbaFilters.log*
EOSKRBA_WEBAPP_LOG=/opt/activiti-5.4/apps/apache-tomcat-6.0.32/logs/eoskrba-webapp.log*
LDAP=127.0.0.1
LIB=lib
# output filenames
OUTPUT_FOLDER=out
ACT_VAL=valuesACT.csv
ACT_CRITICAL=criticalACT.csv
ACT_SMS=messagesACT.csv
PEF_VAL=valuesPEF.csv
PEF_CRITICAL=criticalPEF.csv
PEF_SMS=messagesPEF.csv
NOT_RESPONDING=CMcalls.csv
TASKS_TIMES=taskTimes.csv
INFO_PAGES=infoPages.csv
QUESTIONS=questions.csv
THERAPY=therapyAsthma.csv
SMOKING=smokingAsthma.csv
AAINFAMILY=aaInFamilyAsthma.csv
VISITASTHMA=visitsAsthma.csv
VISITDIABETES=visitsDiabetes.csv
VISITHUJSANJE=visitsHujsanje.csv
VISITSPORT=visitsSport.csv
ARTICLEWEIGHTLOSS=dataForArticleWeightLoss.csv
DIABETESUSERINPUTHOME=diabetesUserInputAtHome.csv
SPORTUSERINPUTHOME=sportUserInputAtHome.csv
STAFF=staffPeople.csv
USER_TO_STUDY=userToStudy.csv
PATIENTS=patients.csv

# priht csv headers
function act_values_header {
	echo "Username, Date & Time, ACT value" > $1
}
function act_critical_header {
        echo "Description, Assignee, Start Date & Time" > $1
}
function act_sms_header {
        echo "Date & Time, Username" > $1
}
function pef_values_header {
        echo "Username, Date & Time, PEF value" > $1
}
function pef_critical_header {
        echo "Description, Assignee, Start Date & Time" > $1
}
function pef_sms_header {
        echo "Date & Time, Username" > $1
}
function not_responding_header {
	echo "Study, Username, Time & Date, Message" > $1
}
function taskts_times_header {
        echo "Description, Assignee, Study, Start time, Time spent DDD HH:MM:SS, Delete reason" > $1
}
function info_pages_header {
        echo "Date & Time, IP, Widget, User, Study, Role" > $1
}
function questions_header {
        echo "Topic, Assigned, Closed, Assignee, Author, Date of the first questions, Group, Number of messages" > $1
}
function therapy_header {
        echo "Username, Taking 1, Name 1, Generic name 1, Taking 2, Name 2, Generic name 2, Taking 3, Name 3, Generic name 3" > $1
}
function smoking_header {
        echo "Username, Smoking status, Number of cigateres per day, Starting age, Tried stopping" > $1
}
function aa_in_family_header {
        echo "Username, Asthma in family, Alergy in family " > $1
}
function visit_asthma_header {
	echo "Eksperimentalna skupina, Uporabnik, Dan, Ura, Ime, Priimek, ID, Spol, Rojstni datum, Telesna višina, Telesna teža, E-pošta, Mobi, Zdr. ustanova, Poklic, Izpolnjuje vključ. kriterije, Vlažno stanovanje, Vsakodnevno v hlevu, Druge posebnosti, Astma v družini, Alergije v družini, Kadilski status, Dnevno št. cigaret, Starost ob začetku uživanja (v letih), Poskus prenehanja, Stopnja ogroženosti, Pogostost pitja (v zadnjih 12 mesecih), Količina pijače ob običajnem pitju, Pogostost pitja večjih količin (M 6 ali več; Ž 4 ali več meric), Ocena ogroženosti, Visoko intenzivna vadba, Zmerno intenzivna vadba, Komentar, Domače živali, Alergije, Alergije ostalo, Druge bolezni, Druga zdravila, Koliko časa vam je v zadnjih 4. tednih astma onemogočala da bi v službi šoli univerzi ali doma izvajali svoje običajne aktivnosti?, Kolikokrat ste v zadnjih 4. Tednih imeli kratko sapo?, Kolikokrat v zadnjih 4. tednih so vas ponoči ali zgodaj zjutraj zbudili simptomi astme (piskanje kašelj dušenje stiskanje ali bolečine v prsih)?, Kolikokrat ste v zadnjih 4. tednih uporabili olajševalec v pršilu ali inhalaciji (kot so Ventolin® Berotec® in Berodual®)?, Kako bi ocenili vaš nadzor nad astmo v zadnjih 4. tednih?, PEF (l/min), FEV1 (l), VC (l), Diagnoza, Opis terapije, Ime zdravila, Generično ime, Število pakiranj, Jemanje - Količina (vdih/število/mg), Pogostost jemanja, Ime zdravila, Generično ime, Število pakiranj, Jemanje - Količina (vdih/število/mg), Pogostost jemanja, Ime zdravila, Generično ime, Število pakiranj, Jemanje - Količina (vdih/število/mg), Pogostost jemanja, Druga zdravila" > $1
}
function visit_diabetes_header {
	echo "Eksperimentalna skupina, Uporabnik, Dan, Ura, Ime, Priimek, ID, Spol, Rojstni datum, Telesna višina, Telesna teža, E-pošta, Mobi, Zdr. ustanova, Poklic, Izpolnjuje ostale vključitvene kriterije (vsaj eden), Ali bolnik uporablja inzulin, Ali bolnik uporablja merilec krvnega sladkorja, Povzetek, Diabetes v družini, Datum začetka bolezni, Tip sladkorne bolezni, Kako pogosto si oseba sama meri telesno težo, Kako pogosto si oseba sama meri krvni sladkor, Kako pogosto si oseba sama meri krvni tlak, Očesno ozadje, Pregled nog, Zgodovina bolezni, Krvni tlak - sistolični, Krvni tlak - diastolični, Krvni tlak - opombe, Hb1Ac, Glukoza v krvi na tešče, Albumin, Aceton, Holesterol, Trigliceridi, HDL, LDL, Kreatinin, Alt, Telesno počutje, Čustva, Vsakodnevna opravila, Družabno življenje, Sprememba zdravja, Splošno stanje, Bolečina, Kadilski status, Dnevno število cigaret, Starost ob začetku uživanja (v letih), Poskus prenehanja, Stopnja ogroženosti, Pogostost pitja (v zadnjih 12 mesecih), Količina pijače ob običajnem pitju, Pogostost pitja večjih količin (M 6 ali več 4 ali več meric), Ocena ogroženosti, Visoko intenzivna vadba (X tedensko), Zmerno intenzivna vadba (X tedensko), Komentar, Diagnoza (prosti tekst), Diagnoza (MKB10), Opis terapije, Zdravila ločena z ### (generično ime; število pakiranj; količina (št/mg/ml/vdih); pogostost jemanja)" > $1
}
function visit_hujsanje_header {
	echo "Eksperimentalna skupina, Uporabnik, Dan, Ura, Ime, Priimek, ID, Spol, Rojstni datum, Telesna višina, Telesna teža, E-pošta, Mobi, Zdr. ustanova, Poklic, Izpolnjuje ostale vključitvene kriterije (vsaj eden), Datum pričetka delavnice, Krvni tlak - sistolični, Krvni tlak - diastolični, Krvni tlak - opombe, Stopnja dnevne aktivnosti, Komentar, Kadilski status, Dnevno število cigaret, Starost ob začetku uživanja (v letih), Poskus prenehanja, Stopnja ogroženosti, Pogostost pitja (v zadnjih 12 mesecih), Količina pijače ob običajnem pitju, Pogostost pitja večjih količin (M 6 ali več 4 ali več meric), Ocena ogroženosti, Visoko intenzivna vadba (X tedensko), Zmerno intenzivna vadba (X tedensko), Komentar, Glukoza v krvi na tešče, Holesterol, Trigliceridi, HDL, LDL, Odstotek telesne maščobe (%), Odstotek trebušne (visceralne) maščobe (%), Odstotek skeletnega mišičevja (%), Poraba metabolizma v mirovanju (BMR) (kcal/dan)" > $1
}
function visit_sport_header {
	echo "Eksperimentalna skupina, Uporabnik, Dan, Ura, Ime, Priimek, ID, Spol, Rojstni datum, Telesna višina, Telesna teža, E-pošta, Mobi, Zdr. ustanova, Poklic, Izpolnjuje ostale vključitvene kriterije (vsaj eden), Obseg pasu (cm), Obseg bokov (cm), Odstotek telesne maščobe (%), Odstotek trebušne (visceralne) maščobe (%), Odstotek skeletnega mišičevja (%), Poraba metabolizma v mirovanju (BMR) (kcal/dan), Stopnja dnevne aktivnosti, Komentar, Datum testa, Cooperjev test 12 min (m), Cooperjev test 2400 m (mm:ss), Maksimalna sila stiska zapestja (N), Maksimalni navor iztegovalk kolena (Nm), Maksimalni navor upogibalk kolena (Nm), Povprečna odrivna moč v prvih 50 ms, Maksimalna višina navpičnega skoka (cm), Razmerje med impulzom sile v drugi in prvi polovici navpičnega odriva (%), Dolžina nihanja telesa v mirovanju (cm), Trajanje testa na tenziometrijski plošči (mm.ss), Počep s palico nad glavo, Korak čez oviro, Izpadni korak s palico za hrbtom, Upogib kolka leže, Mobilnost ramen, Skleci, Klek z dotikom in komolca kolen, Indeks funkcionalnosti gibanja (0-21), Bolečinski test za rame, Bolečinski test za zgornji del hrbta, Bolečinski test za spodnji del hrbta, Interpretacija, Število visoko intenzivnih vadb na teden (X teden), Število zmerno intenzivnih vadb na teden (X teden), Komentar, Kadilski status, Dnevno št. cigaret, Starost ob začetku uživanja (v letih), Poskus prenehanja, Stopnja ogroženosti, Pogostost pitja (v zadnjih 12 mesecih), Količina pijače ob običajnem pitju, Pogostost pitja večjih količin (M 6 ali več; Ž 4 ali več meric), Ocena ogroženosti, Vrsta programa vadbe, Začetni dan vadbe" > $1
}

function article_weight_loss_header {
	echo ",,,,,,,,, Pred študijo,,,,,,,,,,,, Po študiji" > $1
	echo "Ime, Priime, BIS identifikator, Mail, Spol, Datum rojstva, Poklic, Datum vključitve, Odgovorna sestra, Število zaključenih korakov, Telesna teža, Višina, Skeletno mišičevje, Poraba metabolizma v mirovanju, Telesne maščobe (%), Trebušna (visceralna) maščoba (%), Sladkor v krvi, Trigliceridi, Holesterol, HDL, LDL, Krvni tlak, Telesna teža, Višina, Skeletno mišičevje, Poraba metabolizma v mirovanju, Telesne maščobe (%), Trebušna (visceralna) maščoba (%), Sladkor v krvi, Trigliceridi, Holesterol, HDL, LDL, Krvni tlak, Število vsej prijav v aplikacijo, Število kontaktov z odgovorno sestro - pogovor začne udeleženec, Število ogledov dodatnih vsebin, Vse telesne teže vpisane s strani uporabnika (datum - teža ; datum - teža; ...), Vsi obsegi trebuha vpisani s strani uporabnika (datum - obseg ; datum - obseg; ...)" >> $1
}

function diabetes_user_input_at_home_header {
	echo "Uporabnik, Datum, Ura, Teža (v kg), Vrsta vadbe, Intenzivnost vadbe, Trajanje vadbe (format HH:MM), Komentar, Prehrana, Dietni prekrški, Sistolični (v mmHg), Diastolični (v mmHg), Opombe, Ali ste vzeli zadnji odmerek zdravil?, Ali ste danes vzeli vsa predpisana zdravila?, Krvni sladkor (v mmol/L), Čas od zadnjega zaužitega obroka do meritve krvnega sladkorja, Opombe, Katera fizična aktivnost; ki se jo lahko opravljali vsaj dve minuti je bila v zadnjih dveh tednih najtežja?, Kako močno so vas v zadnjih dveh tednih pestile čustvene težave; kot so občutek tesnobe; depresije; razdražljivosti; potrtost ali žalost?, Koliko težav ste imeli v zadnjih dveh tednih pri svojih običajnih dejavnosti doma in drugje zaradi svojega telesnega in duševnega zdravja?, Koliko je v zadnjih dveh tednih vaše telesno in duševno zdravje omejevalo vaše družabne aktivnosti z družino; prijatelji; sosedi ali drugimi skupinami?, Kako bi ocenili svoje trenutno telesno in duševno zdravstveno stanje glede na tisto pred dvemi tedni?, Kako bi na splošno ocenili svoje telesno in duševno zdravstveno stanje zadnja dva tedna?, Koliko telesnih bolečin ste imeli v zadnjih štirih tednih oziroma kako izrazite so bile?" > $1
}

function sport_user_input_at_home_header {
	echo "Uporabnik, Datum vnosa, Ura vnosa, Teža (v kg), Stanje oblačil, Drugi dejavniki, Datum vadbe, Športna panoga, Trajanje vadbe (format HH:MM), Razdalja (v m), Porabljena energija (v kcal), Intenzivnost vadbe, Počutje med vadbo, Opombe" > $1
}

function add_study {
	echo -e "\t\t adding study column to file" $1
	DICT=$OUTPUT_FOLDER/$USER_TO_STUDY
	NUM=$(wc -l $DICT| cut -f1 -d' ')
	I=0
	while read line
	do
		username=$(echo -n $line | sed -e 's/\(.*\),.*/\1/g ; s/ //g')
		((I++))
		echo -e -n "\r\t\t progress: "$I"/"$NUM " user:" $username
		echo -e -n "                                                 "
		sed -i "/\(.*\)$username\(.*\)/ s/\(.*\)$username\(.*\)/\1$line\2/g" $1
	done < $DICT
	echo -e "\r\t\t done!                                                "
}
function add_experimental_or_control_group {
	# set group for file in $1
	# assumes that the username is the first thing in each line
	echo -e "\t\t adding groups to file " $1
	sed -i -e '2,$ s/^/NE,/' $1

	for u in $(ldapsearch -h $LDAP -b "dc=eoskrba,dc=pint,dc=upr,dc=si" "(|(memberatt=patient))" -x | grep "uidAtt:" |sed "s/uidAtt: //g")	
	do
		sed -i "/NE,$u/ s/NE,$u/DA,$u/g" $1
	done
}
function delete_initial_values_clanek {
	sed -i "s/\(.*\)NLOGIN\(.*\)/\1 0 \2/g" $OUTPUT_FOLDER/$ARTICLEWEIGHTLOSS
	sed -i "s/\(.*\)POGOVOR_UDELEZENEC\(.*\)/\1 0 \2/g" $OUTPUT_FOLDER/$ARTICLEWEIGHTLOSS
	sed -i "s/\(.*\)NVSEBIN\(.*\)/\1 0 \2/g" $OUTPUT_FOLDER/$ARTICLEWEIGHTLOSS
	sed -i "s/\(.*\)ODGOVORNA_SESTRA\(.*\)/\1 \2/g" $OUTPUT_FOLDER/$ARTICLEWEIGHTLOSS
}

echo "Report generation started!"
echo "Currently working on:"

# prepare empty folder for export
echo -e "\t preparing output folder"
if [ -d $OUTPUT_FOLDER ] ; then
	rm -r $OUTPUT_FOLDER
fi
mkdir $OUTPUT_FOLDER

echo -e "\t user -> study connection ..."
for u in $(ldapsearch -h $LDAP -b "dc=eoskrba,dc=pint,dc=upr,dc=si" "objectClass=*" -x uidAtt | grep "uidAtt:" |sed "s/uidAtt: //g")
do
        type=$(ldapsearch -h $LDAP -b "dc=eoskrba,dc=pint,dc=upr,dc=si" "uidAtt=$u" -x employeeType |  grep "employeeType:" | sed "s/employeeType: //g")
        echo $u"," $type >> $OUTPUT_FOLDER/$USER_TO_STUDY
done

echo -e "\t ACT values ..."
act_values_header $OUTPUT_FOLDER/$ACT_VAL
$LIB/client.sh -q $LIB/xqueryACT | grep "[^,]*,[^,]*,[^,]*$" >> $OUTPUT_FOLDER/$ACT_VAL

echo -e "\t critical ACT values ..."
act_critical_header $OUTPUT_FOLDER/$ACT_CRITICAL
sudo -u postgres psql -d activiti -f $LIB/criticalACT.sql | grep "Bolnik" | sed -e 's/|/,/g ; s/ Bolnik //g' >> $OUTPUT_FOLDER/$ACT_CRITICAL

echo -e "\t ACT SMS reminders ..."
act_sms_header $OUTPUT_FOLDER/$ACT_SMS
grep -r "SMS ACT reminder sent to" $EOSKRBA_LOG | sed -e 's/\/[^:]*:\([^,]*\).*to\([^,]*\).*/\1,\2/g' >> $OUTPUT_FOLDER/$ACT_SMS

echo -e "\t PEF values ..."
pef_values_header $OUTPUT_FOLDER/$PEF_VAL
$LIB/client.sh -q $LIB/xqueryPEF | grep "[^,]*,[^,]*,[^,]*$" >> $OUTPUT_FOLDER/$PEF_VAL

echo -e "\t critical PEF values ..."
pef_critical_header $OUTPUT_FOLDER/$PEF_CRITICAL
sudo -u postgres psql -d activiti -f $LIB/criticalPEF.sql | grep "Bolnik" | sed -e 's/|/,/g ; s/ Bolnik //g' >> $OUTPUT_FOLDER/$PEF_CRITICAL

echo -e "\t PEF SMS reminders ..."
pef_sms_header $OUTPUT_FOLDER/$PEF_SMS
grep -r "SMS PEF reminder sent to" $EOSKRBA_LOG | sed -e 's/\/.*log.\([^:]*\).*to \([^,]*\).*/\1, \2/g' >> $OUTPUT_FOLDER/$PEF_SMS

echo -e "\t CM calls ..."
not_responding_header $OUTPUT_FOLDER/$NOT_RESPONDING
$LIB/client.sh -q $LIB/xqueryNotResponding | grep "[^,]*,[^,]*,[^,]*$" >> $OUTPUT_FOLDER/$NOT_RESPONDING

echo -e "\t times spent for tasks completition ..."
taskts_times_header $OUTPUT_FOLDER/$TASKS_TIMES
sudo -u postgres psql -d activiti -f $LIB/tasksTimes.sql | sed -e "1d ; 2d ; s/|/,/g" | head -n -2 >> $OUTPUT_FOLDER/$TASKS_TIMES
add_study $OUTPUT_FOLDER/$TASKS_TIMES

# If file quick_hack exist, it is called. Used for some qick data fixing/replacing.
# File quick_hack.sh is NOT included beacuse it contains confident data.
if [ -f $LIB/quick_hack.sh ];
then
	$LIB/quick_hack.sh $OUTPUT_FOLDER/$TASKS_TIMES
fi

echo -e "\t info pages access ..."
info_pages_header $OUTPUT_FOLDER/$INFO_PAGES
grep "learningContent" $EOSKRBA_FILTERS_LOG | grep -v "updateLearningContent\|addLearningContent\|openFileManagerConnector" | sed -e 's/.*Date ://g ; s/ IP_:/,/g ; s/ Controller: /, /g ; s/learningContentReaderWidget .* User:/learningContentReaderWidget, /g ; s/learningContentWriterWidget .* User:/learningContentWriterWidget, /g ; s/ Role:/,/g ; s/ Params:.*//g' >> $OUTPUT_FOLDER/$INFO_PAGES
add_study $OUTPUT_FOLDER/$INFO_PAGES

# If file quick_hack exist, it is called. Used for some qick data fixing/replacing.
# File quick_hack.sh is NOT included beacuse it contains confident data.
if [ -f $LIB/quick_hack.sh ];
then
	$LIB/quick_hack.sh $OUTPUT_FOLDER/$INFO_PAGES
fi

echo -e "\t questions info ..."
questions_header $OUTPUT_FOLDER/$QUESTIONS
sudo -u postgres psql -d eOskrba-webapp -f $LIB/questions.sql | sed -e '1d ; 2d; s/|/,/g;' | head -n -2 >> $OUTPUT_FOLDER/$QUESTIONS

echo -e "\t therapy info ..."
therapy_header $OUTPUT_FOLDER/$THERAPY
$LIB/client.sh -q $LIB/xqueryTH | grep ".*,.*,.*,.*" >> $OUTPUT_FOLDER/$THERAPY

echo -e "\t smoking info ..."
smoking_header $OUTPUT_FOLDER/$SMOKING
$LIB/client.sh -q $LIB/xquerySmoking | grep ".*,.*,.*,.*,.*" >> $OUTPUT_FOLDER/$SMOKING

echo -e "\t asthma or alergy in family ..."
aa_in_family_header $OUTPUT_FOLDER/$AAINFAMILY
$LIB/client.sh -q $LIB/xqueryFamiliy | grep ".*,.*,.*" >> $OUTPUT_FOLDER/$AAINFAMILY

echo -e "\t asthma visits ..."
visit_asthma_header $OUTPUT_FOLDER/$VISITASTHMA
$LIB/client.sh -q $LIB/xqueryVisitAsthma | grep ".*,.*,.*" >> $OUTPUT_FOLDER/$VISITASTHMA
add_experimental_or_control_group $OUTPUT_FOLDER/$VISITASTHMA

echo -e "\t diabetes visits ..."
visit_diabetes_header $OUTPUT_FOLDER/$VISITDIABETES
$LIB/client.sh -q $LIB/xqueryVisitDiabetes | grep ".*,.*,.*" >> $OUTPUT_FOLDER/$VISITDIABETES
add_experimental_or_control_group $OUTPUT_FOLDER/$VISITDIABETES

echo -e "\t hujsanje visits ..."
visit_hujsanje_header $OUTPUT_FOLDER/$VISITHUJSANJE
$LIB/client.sh -q $LIB/xqueryVisitHujsanje | grep ".*,.*,.*" >> $OUTPUT_FOLDER/$VISITHUJSANJE
add_experimental_or_control_group $OUTPUT_FOLDER/$VISITHUJSANJE

echo -e "\t sport visits ..."
visit_sport_header $OUTPUT_FOLDER/$VISITSPORT
$LIB/client.sh -q $LIB/xqueryVisitSport | grep ".*,.*,.*" >> $OUTPUT_FOLDER/$VISITSPORT
add_experimental_or_control_group $OUTPUT_FOLDER/$VISITSPORT

echo -e "\t weight loss data for article ..."
article_weight_loss_header $OUTPUT_FOLDER/$ARTICLEWEIGHTLOSS
$LIB/client.sh -q $LIB/xqueryClanekHujsanje | grep ".*,.*,.*" >> $OUTPUT_FOLDER/$ARTICLEWEIGHTLOSS

echo -e "\t diabetes user input at home ..."
diabetes_user_input_at_home_header $OUTPUT_FOLDER/$DIABETESUSERINPUTHOME 
$LIB/client.sh -q $LIB/xqueryDiabetesVnosiUporabnika | grep ".*,.*,.*" >> $OUTPUT_FOLDER/$DIABETESUSERINPUTHOME

echo -e "\t sport user input at home ..."
sport_user_input_at_home_header $OUTPUT_FOLDER/$SPORTUSERINPUTHOME
$LIB/client.sh -q $LIB/xquerySportVnosiUporabnika | grep ".*,.*,.*" >> $OUTPUT_FOLDER/$SPORTUSERINPUTHOME

echo -e "\t loading caremanagers and selecting data ..."
ldapsearch -h $LDAP -b "dc=eoskrba,dc=pint,dc=upr,dc=si" "(|(memberatt=caremanager)(memberatt=doctor))" -x | grep "uidAtt:" | sed -e 's/uidAtt: //' >> $OUTPUT_FOLDER/$STAFF

# for each staff member
NUM=$(wc -l $OUTPUT_FOLDER/$STAFF| cut -f1 -d' ')
I=0
for sm in $(cat $OUTPUT_FOLDER/$STAFF)
do
	# print progress
	((I++))
        echo -e -n "\r\t\t progress: "$I"/"$NUM " staff member:" $sm
        echo -e -n "                                                 "
	
	# make folder
	mkdir $OUTPUT_FOLDER/$sm
	
	# print headers
	act_values_header $OUTPUT_FOLDER/$sm/$ACT_VAL
	act_critical_header $OUTPUT_FOLDER/$sm/$ACT_CRITICAL
	act_sms_header $OUTPUT_FOLDER/$sm/$ACT_SMS
	pef_values_header $OUTPUT_FOLDER/$sm/$PEF_VAL
	pef_critical_header $OUTPUT_FOLDER/$sm/$PEF_CRITICAL
	pef_sms_header $OUTPUT_FOLDER/$sm/$PEF_SMS
	not_responding_header $OUTPUT_FOLDER/$sm/$NOT_RESPONDING
	taskts_times_header $OUTPUT_FOLDER/$sm/$TASKS_TIMES
	info_pages_header $OUTPUT_FOLDER/$sm/$INFO_PAGES
	questions_header $OUTPUT_FOLDER/$sm/$QUESTIONS
	therapy_header $OUTPUT_FOLDER/$sm/$THERAPY
	smoking_header $OUTPUT_FOLDER/$sm/$SMOKING
	aa_in_family_header $OUTPUT_FOLDER/$sm/$AAINFAMILY
	visit_asthma_header $OUTPUT_FOLDER/$sm/$VISITASTHMA	
	visit_diabetes_header $OUTPUT_FOLDER/$sm/$VISITDIABETES
	visit_hujsanje_header $OUTPUT_FOLDER/$sm/$VISITHUJSANJE
	visit_sport_header $OUTPUT_FOLDER/$sm/$VISITSPORT
	diabetes_user_input_at_home_header $OUTPUT_FOLDER/$sm/$DIABETESUSERINPUTHOME
	sport_user_input_at_home_header $OUTPUT_FOLDER/$sm/$SPORTUSERINPUTHOME

        # get patients
        P=$(ldapsearch -h $LDAP -b "dc=eoskrba,dc=pint,dc=upr,dc=si" "patientsDoctorAtt=$sm" -x | grep "uidAtt:" | sed -e 's/uidAtt: //')
        echo $P > $OUTPUT_FOLDER/$sm/$PATIENTS

	for p in $P
	do
		# filtering by patients
		grep "$p" $OUTPUT_FOLDER/$ACT_VAL >> $OUTPUT_FOLDER/$sm/$ACT_VAL
		grep "$p" $OUTPUT_FOLDER/$ACT_SMS >> $OUTPUT_FOLDER/$sm/$ACT_SMS
		grep "$p" $OUTPUT_FOLDER/$PEF_VAL >> $OUTPUT_FOLDER/$sm/$PEF_VAL
		grep "$p" $OUTPUT_FOLDER/$PEF_SMS >> $OUTPUT_FOLDER/$sm/$PEF_SMS
		grep "$p" $OUTPUT_FOLDER/$NOT_RESPONDING >> $OUTPUT_FOLDER/$sm/$NOT_RESPONDING
		grep "$p" $OUTPUT_FOLDER/$INFO_PAGES >> $OUTPUT_FOLDER/$sm/$INFO_PAGES
		# unassigned questions
		grep ", f *, f *" $OUTPUT_FOLDER/$QUESTIONS | grep "$p" >> $OUTPUT_FOLDER/$sm/$QUESTIONS
		grep "$p" $OUTPUT_FOLDER/$THERAPY >> $OUTPUT_FOLDER/$sm/$THERAPY
		grep "$p" $OUTPUT_FOLDER/$SMOKING >> $OUTPUT_FOLDER/$sm/$SMOKING
		grep "$p" $OUTPUT_FOLDER/$AAINFAMILY >> $OUTPUT_FOLDER/$sm/$AAINFAMILY
		grep "$p" $OUTPUT_FOLDER/$VISITASTHMA >> $OUTPUT_FOLDER/$sm/$VISITASTHMA	
		grep "$p" $OUTPUT_FOLDER/$VISITDIABETES >> $OUTPUT_FOLDER/$sm/$VISITDIABETES
		grep "$p" $OUTPUT_FOLDER/$VISITHUJSANJE >> $OUTPUT_FOLDER/$sm/$VISITHUJSANJE
		grep "$p" $OUTPUT_FOLDER/$VISITSPORT >> $OUTPUT_FOLDER/$sm/$VISITSPORT
		grep "$p" $OUTPUT_FOLDER/$DIABETESUSERINPUTHOME >> $OUTPUT_FOLDER/$sm/$DIABETESUSERINPUTHOME
		grep "$p" $OUTPUT_FOLDER/$SPORTUSERINPUTHOME >> $OUTPUT_FOLDER/$sm/$SPORTUSERINPUTHOME	

		sed -i "/\(.*\)$p\(.*\)/ s/\(.*\)ODGOVORNA_SESTRA\(.*\)/\1$sm\2/g" $OUTPUT_FOLDER/$ARTICLEWEIGHTLOSS

		NLOGIN=$(grep "Uspešen login" $EOSKRBA_WEBAPP_LOG | grep "$p" | wc -l | cut -f1 -d' ')
		# echo $p "->" $NLOGIN
		sed -i "/\(.*\)$p\(.*\)/ s/\(.*\)NLOGIN\(.*\)/\1$NLOGIN\2/g" $OUTPUT_FOLDER/$ARTICLEWEIGHTLOSS

		POGOVOR_UDELEZENEC=$(grep ".*,.*,.*,.*,.*$p.*" $OUTPUT_FOLDER/$QUESTIONS | wc -l | cut -f1 -d' ')
		# echo $p "->" $POGOVOR_UDELEZENEC
		sed -i "/\(.*\)$p\(.*\)/ s/\(.*\)POGOVOR_UDELEZENEC\(.*\)/\1$POGOVOR_UDELEZENEC\2/g" $OUTPUT_FOLDER/$ARTICLEWEIGHTLOSS

		NVSEBIN=$(grep "Dodatna izobrazevalna vsebina" $EOSKRBA_WEBAPP_LOG | grep "$p" | wc -l | cut -f1 -d' ')
		# echo $p "->" $NVSEBIN
		sed -i "/\(.*\)$p\(.*\)/ s/\(.*\)NVSEBIN\(.*\)/\1$NVSEBIN\2/g" $OUTPUT_FOLDER/$ARTICLEWEIGHTLOSS

	done
	
	# filtering by sm
	grep "$sm" $OUTPUT_FOLDER/$ACT_CRITICAL >> $OUTPUT_FOLDER/$sm/$ACT_CRITICAL
        grep "$sm" $OUTPUT_FOLDER/$PEF_CRITICAL >> $OUTPUT_FOLDER/$sm/$PEF_CRITICAL	
	grep "$sm" $OUTPUT_FOLDER/$TASKS_TIMES >> $OUTPUT_FOLDER/$sm/$TASKS_TIMES	
	grep "$sm" $OUTPUT_FOLDER/$QUESTIONS >> $OUTPUT_FOLDER/$sm/$QUESTIONS
done

delete_initial_values_clanek

echo -e "\r\t\t done!                                                "
echo "Report generation finished!"
echo "Results are located in folder:" $OUTPUT_FOLDER

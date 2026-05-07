
echo "======================="
echo "Creation des domaines"
echo "======================="
echo " "

create-domain marmotte
create-domain loup
create-domain renard

echo " "
echo "======================="
echo "Ajout du plan BEGINNER_PASS au domain loup"
echo "======================="
echo " "

add-plan-domain 2 BEGINNER_PASS

echo " "
echo "======================="
echo "Enregistrement de portiques"
echo "======================="
echo " "

register-gate gate1 1
register-gate gate2 1
register-gate gate3 2
register-gate gate4 3

echo " "
echo "======================="
echo "Enregistrement du panneau"
echo "======================="
echo " "
register-panel panel1

echo " "
echo "======================="
echo "Enregistrement des portiques sur le panneau "
echo "======================="
echo " "

add-panel-gate panel1 gate1
add-panel-gate panel1 gate2
add-panel-gate panel1 gate3
add-panel-gate panel1 gate4

echo " "
echo "======================="
echo "Creation de comptes adultes"
echo "======================="
echo " "

register jean 5251896983 ADULT
register pierre 5251896983 ADULT
register paul 5251896983 ADULT

echo " "
echo "======================="
echo "Achat de forfaits"
echo "======================="
echo " "

add-to-cart jean BEGINNER_PASS 1
validate-cart jean

add-to-cart pierre BASIC_PLAN 1
validate-cart pierre

add-to-cart paul SUPER_CARD 1
validate-cart paul

echo " "
echo "======================="
echo "Ouverture des portiques"
echo "======================="
echo " "

gate-open gate1
gate-open gate2
gate-open gate3
gate-open gate4

echo " "
echo "======================="
echo "Envoie initial des cartes nfc sur les portiques"
echo "======================="
echo " "

setup-gates-cards
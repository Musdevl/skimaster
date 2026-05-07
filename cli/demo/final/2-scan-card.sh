echo "======================="
echo "Scan des cartes sur les deux gates du domaine 1 ( sans restriction )"
echo "======================="
echo ""
scan-card gate1 1
scan-card gate1 2
scan-card gate2 3

echo ""
echo "===================="
echo "Scan des cartes sur le domaine reservé aux débutant"
echo "======================="
echo ""

scan-card gate3 1
scan-card gate3 2
scan-card gate3 3

echo ""
echo "======================="
echo "Seul jean (id=1) peut passer"
echo "======================="
echo ""
echo "======================="
echo "Scan de carte pour un client avec SUPER_CARD"
echo "======================="
echo ""

scan-card gate1 3
scan-card gate2 3
scan-card gate4 3

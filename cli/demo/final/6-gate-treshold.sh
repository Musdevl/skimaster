echo "======================="
echo "Setup de la jauge depuis le centre de contrôle"
echo "======================="
echo ""
set-gate-threshold gate1 2 10

dashboard-watch 5

echo "======================="
echo "Re ouverture du portique"
echo "======================="
gate-open gate1
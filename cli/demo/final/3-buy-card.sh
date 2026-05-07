echo "======================="
echo "Achat d'un pack"
echo "======================="
echo ""
register jeanFamille 5251896983 ADULT

add-to-cart jeanFamille FAMILY_PLAN 1

validate-cart jeanFamille

echo ""
echo "======================="
echo "Scan de carte de toute la famille (enfant et adultes)"
echo "======================="
echo ""

scan-card gate1 4
scan-card gate1 5
scan-card gate1 6
scan-card gate1 7

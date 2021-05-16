# Combine downloaded datasets from 2020 and 2021
#
# https://vaers.hhs.gov/data/datasets.html
#
# Usage:
#
# $ sh ./combine.sh

combined_VAERSData="VAERSData.csv"
combined_VAERSVAX="VAERSVAX.csv"
VAERSData2020="2020VAERSData.csv"
VAERSVAX2020="2020VAERSVAX.csv"
VAERSData2021="2021VAERSData.csv"
VAERSVAX2021="2021VAERSVAX.csv"

# Remove existing combined files.
[ -e "$combined_VAERSData" ] && rm "$combined_VAERSData"
[ -e "$combined_VAERSVAX" ] && rm "$combined_VAERSVAX"

# Initialize new combined files with 2020 data
[ -e "$VAERSData2020" ] && cp "$VAERSData2020" "$combined_VAERSData"
[ -e "$VAERSVAX2020" ] && cp "$VAERSVAX2020" "$combined_VAERSVAX"

# Strip headers and append 2021 data
sed '1d' "$VAERSData2021" >> "$combined_VAERSData"
sed '1d' "$VAERSVAX2021" >> "$combined_VAERSVAX"

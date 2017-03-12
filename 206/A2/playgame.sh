#!/bin/bash
#set -x
# Camilo Garcia La Rotta
# ID 260657037
# Q2

### Global Constants

# file for data persistence
OUTPUT_FILE="Data.pg"

# key to encrypt/decrypt numbers in OUTPUT_FILE
CESAR_KEY=5

### Helper Methods
 
# read lines $1 to $2 of file and calculate average
function calculate_avg {
  
  readarray tmp_array < $OUTPUT_FILE
 
  # this script is used in two scenarios: 
  # - for random num avg
  # - for tries avg
  # in the case of the tries avg:
  # I have to implement this fix to make sure the size over which we will average
  # is the amount of non-null tries. i.e. when only 3 players have played 
  # we shouldnt average over the past 5 tries but rather the only 3 non null entries
  
  # determine the range of numbers over which we will average
  if [[ $1 -eq 11 ]]; then
    # average of tries

    # the actual amount of tries is equal to
    # total_lines - 10 random num lines - 1 separator line
    total_num_lines=$(wc -l < "$OUTPUT_FILE")
    size=$(( total_num_lines - 11 ))
  else
    # average of random numbers
    size=$(( $2-$1 ))
  fi

  count=0
    
  for (( i=$1;i<$2;i++ )); do
    tmp_val=${tmp_array[$i]}
    (( count += tmp_val ))
  done
  
  echo $(( (count/size) - CESAR_KEY )) 
}

# float to integer transformation
function float_to_int() {
  printf "%.0f" "$1"
}

### Starting point of script

# verify script is run without arguments
test "$#" -eq 0 || { echo "This script runs without arguments" && exit 1; }

# only create new set of random numbers if $OUTPUT_FILE doesn't exist
if ! [[ -e $OUTPUT_FILE ]]; then
  
  touch $OUTPUT_FILE

  # generate random numbers
  for i in $(seq 1 10);  do
    num=$(( ($RANDOM % 50) + (1 + CESAR_KEY) ))
    printf "%d\n" "$num" >> $OUTPUT_FILE
  done
  
  # add a separator between random numbers and recorded tries
  # note that this is merely a visual aid, the script doesnt need it
  printf "\n" >> $OUTPUT_FILE

  # initial average of tries is 1 
  printf "%d\n" "$((1 + CESAR_KEY))" >> $OUTPUT_FILE
fi

# give 3 tries to find correct value
tries=0
while [ $tries -lt 3 ]; do
  
  (( tries++ ))
  
  # calculate current average
  curr_avg=$(calculate_avg 0 10)
  
  # get current tries average (by default last line of file)
  avg_tries=$(calculate_avg 11 16)

  #echo $curr_avg
  
  # prompt user 
  printf "Guess the number: "
  read guess
 
  # calculate +/-10% of average
  lower_bound_float=$( echo "$curr_avg * 0.9" | bc )
  higher_bound_float=$( echo "$curr_avg * 1.1" | bc )

  lower_bound=$(float_to_int ${lower_bound_float})
  higher_bound=$(float_to_int ${higher_bound_float})

  #echo $lower_bound $higher_bound 
  
  if [[ $lower_bound -le $guess && $higher_bound -ge $guess ]]; then
    
    # user guessed correctly
    echo "Well done. you took ${tries} tries. Average tries is ${avg_tries}" 
    
    # store user's number of tries
    cyphered_tries=$((tries + CESAR_KEY))
    sed -i -e 12i\\${cyphered_tries} -e 16d ${OUTPUT_FILE}
    
    break 
  else 
    # generate random line to overwrite
    line=$(( ($RANDOM % 10)+1 ))
    #echo $line

    # replace line with users guess
    cyphered_guess=$((guess + CESAR_KEY))
    sed -i -e ${line}i\\${cyphered_guess} -e ${line}d ${OUTPUT_FILE}
  fi
  
  # user didn't guess the number after 3 tries, log the amount of tries before exiting
  if [[ $tries -eq 3 ]]; then
    echo "Sorry, you ran out of tries, answer was ${curr_avg}"
    cyphered_tries=$((tries + CESAR_KEY))
    sed -i -e 12i\\${cyphered_tries} -e 16d ${OUTPUT_FILE}
  fi
done

exit 0

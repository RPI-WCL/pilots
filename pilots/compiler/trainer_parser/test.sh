result="./`date "+%m_%d_%T"`.txt"
echo '' > $result
(
		echo 1 && java PilotsParser test/1 > $result &&
		echo 2 && java PilotsParser test/2 > $result &&
		echo 3 && java PilotsParser test/3 > $result &&
		echo 4 && java PilotsParser test/4 > $result &&
		echo 5 && java PilotsParser test/5 > $result &&
		echo 6 && java PilotsParser test/6 > $result &&
		echo result is save to $result &&
		echo pass
) || ( rm -f $result && echo fail)


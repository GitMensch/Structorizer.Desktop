10 REM Creates three equal arrays of numbers and has them sorted with different sorting algorithms 
20 REM to allow performance comparison via execution counting ("Collect Runtime Data" should 
30 REM sensibly be switched on). 
40 REM Requested input data are: Number of elements (size) and filing mode. 
50 REM Generated by Structorizer 3.30-03 
60 REM  
70 REM Copyright (C) 2019-10-02 Kay Gürtzig 
80 REM License: GPLv3-link 
90 REM GNU General Public License (V 3) 
100 REM https://www.gnu.org/licenses/gpl.html 
110 REM http://www.gnu.de/documents/gpl.de.html 
120 REM  
130 REM  
140 REM program SORTING_TEST_MAIN
150 REM TODO: add the respective type suffixes to your variable names if required 
160 DO
170   INPUT elementCount
180 LOOP UNTIL elementCount >= 1
190 DO
200   PRINT "Filling: 1 = random, 2 = increasing, 3 = decreasing"; : INPUT modus
210 LOOP UNTIL modus = 1 OR modus = 2 OR modus = 3
220 FOR i = 0 TO elementCount-1
230   SELECT CASE modus
240     CASE 1
250       LET values1(i) = random(10000)
260     CASE 2
270       LET values1(i) = i
280     CASE 3
290       LET values1(i) = -i
300   END SELECT
310 NEXT i
320 REM Copy the array for exact comparability 
330 FOR i = 0 TO elementCount-1
340   LET values2(i) = values1(i)
350   LET values3(i) = values1(i)
360 NEXT i
370 
380 REM ========================================================== 
390 REM ================= START PARALLEL SECTION ================= 
400 REM ========================================================== 
410 REM TODO: add the necessary code to run the threads concurrently 
420 
430   REM ----------------- START THREAD 0 ----------------- 
440     CALL bubbleSort(values1)
450   REM ------------------ END THREAD 0 ------------------ 
460 
470 
480   REM ----------------- START THREAD 1 ----------------- 
490     CALL quickSort(values2, 0, elementCount)
500   REM ------------------ END THREAD 1 ------------------ 
510 
520 
530   REM ----------------- START THREAD 2 ----------------- 
540     CALL heapSort(values3)
550   REM ------------------ END THREAD 2 ------------------ 
560 
570 REM ========================================================== 
580 REM ================== END PARALLEL SECTION ================== 
590 REM ========================================================== 
600 
610 LET ok1 = testSorted(values1)
620 LET ok2 = testSorted(values2)
630 LET ok3 = testSorted(values3)
640 IF NOT ok1 OR NOT ok2 OR NOT ok3 THEN
650   FOR i = 0 TO elementCount-1
660     IF values1(i) <> values2(i) OR values1(i) <> values3(i) THEN
670       PRINT "Difference at ["; i; "]: "; values1(i); " <-> "; values2(i); " <-> "; values3(i)
680     END IF
690   NEXT i
700 END IF
710 DO
720   PRINT "Show arrays (yes/no)?"; : INPUT show
730 LOOP UNTIL show = "yes" OR show = "no"
740 IF show = "yes" THEN
750   FOR i = 0 TO elementCount - 1
760     PRINT "["; i; "]:\t"; values1(i); "\t"; values2(i); "\t"; values3(i)
770   NEXT i
780 END IF
790 END
800 REM  
810 REM Implements the well-known BubbleSort algorithm. 
820 REM Compares neigbouring elements and swaps them in case of an inversion. 
830 REM Repeats this while inversions have been found. After every 
840 REM loop passage at least one element (the largest one out of the 
850 REM processed subrange) finds its final place at the end of the 
860 REM subrange. 
870 REM TODO: Add type-specific suffixes where necessary! 
880 SUB bubbleSort(values)
890   REM TODO: add the respective type suffixes to your variable names if required 
900   LET ende = length(values) - 2
910   DO
920     REM The index of the most recent swapping (-1 means no swapping done). 
930     LET posSwapped = -1
940     FOR i = 0 TO ende
950       IF values(i) > values(i+1) THEN
960         LET temp = values(i)
970         LET values(i) = values(i+1)
980         LET values(i+1) = temp
990         LET posSwapped = i
1000       END IF
1010     NEXT i
1020     LET ende = posSwapped - 1
1030   LOOP UNTIL posSwapped < 0
1040 END SUB
1050 REM  
1060 REM Given a max-heap 'heap´ with element at index 'i´ possibly 
1070 REM violating the heap property wrt. its subtree upto and including 
1080 REM index range-1, restores heap property in the subtree at index i 
1090 REM again. 
1100 REM TODO: Add type-specific suffixes where necessary! 
1110 SUB maxHeapify(heap, i, range)
1120   REM TODO: add the respective type suffixes to your variable names if required 
1130   REM Indices of left and right child of node i 
1140   LET right = (i+1) * 2
1150   LET left = right - 1
1160   REM Index of the (local) maximum 
1170   LET max = i
1180   IF left < range AND heap(left) > heap(i) THEN
1190     LET max = left
1200   END IF
1210   IF right < range AND heap(right) > heap(max) THEN
1220     LET max = right
1230   END IF
1240   IF max <> i THEN
1250     LET temp = heap(i)
1260     LET heap(i) = heap(max)
1270     LET heap(max) = temp
1280     CALL maxHeapify(heap, max, range)
1290   END IF
1300 END SUB
1310 REM  
1320 REM Partitions array values between indices start und stop-1 with 
1330 REM respect to the pivot element initially at index p into smaller 
1340 REM and greater elements. 
1350 REM Returns the new (and final) index of the pivot element (which 
1360 REM separates the sequence of smaller from the sequence of greater 
1370 REM elements). 
1380 REM This is not the most efficient algorithm (about half the swapping 
1390 REM might still be avoided) but it is pretty clear. 
1400 REM TODO: Add type-specific suffixes where necessary! 
1410 FUNCTION partition(values, start, stop, p) AS Integer
1420   REM TODO: add the respective type suffixes to your variable names if required 
1430   LET pivot = values(p)
1440   REM Tausche das Pivot-Element an den start 
1450   LET values(p) = values(start)
1460   LET values(start) = pivot
1470   LET p = start
1480   REM Beginning and end of the remaining unknown range 
1490   LET start = start + 1
1500   LET stop = stop - 1
1510   REM Still unseen elements? 
1520   DO WHILE stop >= start
1530     LET seen = values(start)
1540     IF values(start) <= pivot THEN
1550       REM Swap pivot element with start element 
1560       LET values(p) = seen
1570       LET values(start) = pivot
1580       LET p = p + 1
1590       LET start = start + 1
1600     ELSE
1610       REM Put the found element to the end of the unknown area 
1620       LET values(start) = values(stop)
1630       LET values(stop) = seen
1640       LET stop = stop - 1
1650     END IF
1660   LOOP
1670   return p
1680 END FUNCTION
1690 REM  
1700 REM Checks whether or not the passed-in array is (ascendingly) sorted. 
1710 REM TODO: Add type-specific suffixes where necessary! 
1720 FUNCTION testSorted(numbers) AS bool
1730   REM TODO: add the respective type suffixes to your variable names if required 
1740   LET isSorted = true
1750   LET i = 0
1760   REM As we compare with the following element, we must stop at the penultimate index 
1770   DO WHILE isSorted AND (i <= length(numbers)-2)
1780     REM Is there an inversion? 
1790     IF numbers(i) > numbers(i+1) THEN
1800       LET isSorted = false
1810     ELSE
1820       LET i = i + 1
1830     END IF
1840   LOOP
1850   return isSorted
1860 END FUNCTION
1870 REM  
1880 REM Runs through the array heap and converts it to a max-heap 
1890 REM in a bottom-up manner, i.e. starts above the "leaf" level 
1900 REM (index >= length(heap) div 2) and goes then up towards 
1910 REM the root. 
1920 REM TODO: Add type-specific suffixes where necessary! 
1930 FUNCTION buildMaxHeap(heap)
1940   REM TODO: add the respective type suffixes to your variable names if required 
1950   LET lgth = length(heap)
1960   FOR k = lgth / 2 - 1 TO 0 STEP -1
1970     CALL maxHeapify(heap, k, lgth)
1980   NEXT k
1990   RETURN 0
2000 END FUNCTION
2010 REM  
2020 REM Recursively sorts a subrange of the given array 'values´.  
2030 REM start is the first index of the subsequence to be sorted, 
2040 REM stop is the index BEHIND the subsequence to be sorted. 
2050 REM TODO: Add type-specific suffixes where necessary! 
2060 FUNCTION quickSort(values, start, stop)
2070   REM TODO: add the respective type suffixes to your variable names if required 
2080   REM At least 2 elements? (Less don't make sense.) 
2090   IF stop >= start + 2 THEN
2100     REM Select a pivot element, be p its index. 
2110     REM (here: randomly chosen element out of start ... stop-1) 
2120     LET p = random(stop-start) + start
2130     REM Partition the array into smaller and greater elements 
2140     REM Get the resulting (and final) position of the pivot element 
2150     LET p = partition(values, start, stop, p)
2160     REM Sort subsequances separately and independently ... 
2170 
2180     REM ========================================================== 
2190     REM ================= START PARALLEL SECTION ================= 
2200     REM ========================================================== 
2210     REM TODO: add the necessary code to run the threads concurrently 
2220 
2230       REM ----------------- START THREAD 0 ----------------- 
2240         REM Sort left (lower) array part 
2250         CALL quickSort(values, start, p)
2260       REM ------------------ END THREAD 0 ------------------ 
2270 
2280 
2290       REM ----------------- START THREAD 1 ----------------- 
2300         REM Sort right (higher) array part 
2310         CALL quickSort(values, p+1, stop)
2320       REM ------------------ END THREAD 1 ------------------ 
2330 
2340     REM ========================================================== 
2350     REM ================== END PARALLEL SECTION ================== 
2360     REM ========================================================== 
2370 
2380   END IF
2390   RETURN 0
2400 END FUNCTION
2410 REM  
2420 REM Sorts the array 'values´ of numbers according to he heap sort 
2430 REM algorithm 
2440 REM TODO: Add type-specific suffixes where necessary! 
2450 FUNCTION heapSort(values)
2460   REM TODO: add the respective type suffixes to your variable names if required 
2470   CALL buildMaxHeap(values)
2480   LET heapRange = length(values)
2490   FOR k = heapRange - 1 TO 1 STEP -1
2500     LET heapRange = heapRange - 1
2510     REM Swap the maximum value (root of the heap) to the heap end 
2520     LET maximum = values(0)
2530     LET values(0) = values(heapRange)
2540     LET values(heapRange) = maximum
2550     CALL maxHeapify(values, 0, heapRange)
2560   NEXT k
2570   RETURN 0
2580 END FUNCTION

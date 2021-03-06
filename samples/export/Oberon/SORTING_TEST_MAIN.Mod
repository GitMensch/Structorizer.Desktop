MODULE SORTING_TEST_MAIN;
IMPORT dummy0815;
IMPORT In, Out;
(*
 * Creates three equal arrays of numbers and has them sorted with different sorting algorithms
 * to allow performance comparison via execution counting ("Collect Runtime Data" should
 * sensibly be switched on).
 * Requested input data are: Number of elements (size) and filing mode.
 *)
(* Generated by Structorizer 3.30-03 *)
(*  *)
(* Copyright (C) 2019-10-02 Kay Gürtzig *)
(* License: GPLv3-link *)
(* GNU General Public License (V 3) *)
(* https://www.gnu.org/licenses/gpl.html *)
(* http://www.gnu.de/documents/gpl.de.html *)
(*  *)
VAR
  (* TODO: check and accomplish variable declarations *)
  values3: ARRAY OF ???;	(* FIXME! *)
  values2: ARRAY OF ???;	(* FIXME! *)
  values1: ARRAY OF INTEGER;
  show: ???;	(* FIXME! *)
  ok3: BOOLEAN;
  ok2: BOOLEAN;
  ok1: BOOLEAN;
  modus: ???;	(* FIXME! *)
  i: INTEGER;
  elementCount: ???;	(* FIXME! *)

PROCEDURE bubbleSort*(values: (*type?*));
(*
 * Implements the well-known BubbleSort algorithm.
 * Compares neigbouring elements and swaps them in case of an inversion.
 * Repeats this while inversions have been found. After every
 * loop passage at least one element (the largest one out of the
 * processed subrange) finds its final place at the end of the
 * subrange.
 *)
VAR
  (* TODO: check and accomplish variable declarations *)
  temp: ???;	(* FIXME! *)
  posSwapped: ???;	(* FIXME! *)
  i: INTEGER;
  ende: ???;	(* FIXME! *)
BEGIN
  ende := length(values) - 2;
  REPEAT
    (* The index of the most recent swapping (-1 means no swapping done). *)
    posSwapped := -1;
    FOR i := 0 TO ende DO
      IF values[i] > values[i+1] THEN
        temp := values[i];
        values[i] := values[i+1];
        values[i+1] := temp;
        posSwapped := i;
      END;
    END;
    ende := posSwapped - 1;
  UNTIL posSwapped < 0;
END bubbleSort;

PROCEDURE maxHeapify*(heap: (*type?*); i: (*type?*); range: (*type?*));
(*
 * Given a max-heap 'heap´ with element at index 'i´ possibly
 * violating the heap property wrt. its subtree upto and including
 * index range-1, restores heap property in the subtree at index i
 * again.
 *)
VAR
  (* TODO: check and accomplish variable declarations *)
  temp: ???;	(* FIXME! *)
  (* Indices of left and right child of node i *)
  right: ???;	(* FIXME! *)
  max: ???;	(* FIXME! *)
  (* Indices of left and right child of node i *)
  left: ???;	(* FIXME! *)
BEGIN
  (* Indices of left and right child of node i *)
  right := (i+1) * 2;
  left := right - 1;
  (* Index of the (local) maximum *)
  max := i;
  IF left < range & heap[left] > heap[i] THEN
    max := left;
  END;
  IF right < range & heap[right] > heap[max] THEN
    max := right;
  END;
  IF max # i THEN
    temp := heap[i];
    heap[i] := heap[max];
    heap[max] := temp;
    maxHeapify(heap, max, range);
  END;
END maxHeapify;

PROCEDURE partition*(values: (*type?*); start: (*type?*); stop: (*type?*); p: (*type?*)): INTEGER;
(*
 * Partitions array values between indices start und stop-1 with
 * respect to the pivot element initially at index p into smaller
 * and greater elements.
 * Returns the new (and final) index of the pivot element (which
 * separates the sequence of smaller from the sequence of greater
 * elements).
 * This is not the most efficient algorithm (about half the swapping
 * might still be avoided) but it is pretty clear.
 *)
VAR
  (* TODO: check and accomplish variable declarations *)
  seen: ???;	(* FIXME! *)
  pivot: ???;	(* FIXME! *)
BEGIN
  pivot := values[p];
  (* Tausche das Pivot-Element an den start *)
  values[p] := values[start];
  values[start] := pivot;
  p := start;
  (* Beginning and end of the remaining unknown range *)
  start := start + 1;
  stop := stop - 1;
  (* Still unseen elements? *)
  WHILE stop >= start DO
    seen := values[start];
    IF values[start] <= pivot THEN
      (* Swap pivot element with start element *)
      values[p] := seen;
      values[start] := pivot;
      p := p + 1;
      start := start + 1;
    END
    ELSE
      (* Put the found element to the end of the unknown area *)
      values[start] := values[stop];
      values[stop] := seen;
      stop := stop - 1;
    END;
  END;
  RETURN p;
END partition;

PROCEDURE testSorted*(numbers: (*type?*)): BOOLEAN;
(*
 * Checks whether or not the passed-in array is (ascendingly) sorted.
 *)
VAR
  (* TODO: check and accomplish variable declarations *)
  isSorted: BOOLEAN;
  i: INTEGER;
BEGIN
  isSorted := true;
  i := 0;
  (* As we compare with the following element, we must stop at the penultimate index *)
  WHILE isSorted & (i <= length(numbers)-2) DO
    (* Is there an inversion? *)
    IF numbers[i] > numbers[i+1] THEN
      isSorted := false;
    END
    ELSE
      i := i + 1;
    END;
  END;
  RETURN isSorted;
END testSorted;

PROCEDURE buildMaxHeap*(heap: (*type?*)): ;
(*
 * Runs through the array heap and converts it to a max-heap
 * in a bottom-up manner, i.e. starts above the "leaf" level
 * (index >= length(heap) div 2) and goes then up towards
 * the root.
 *)
VAR
  (* TODO: check and accomplish variable declarations *)
  lgth: INTEGER;
  k: INTEGER;
BEGIN
  lgth := length(heap);
  FOR k := lgth DIV 2 - 1 TO 0 BY -1 DO
    maxHeapify(heap, k, lgth);
  END;
  RETURN 0;
END buildMaxHeap;

PROCEDURE quickSort*(values: (*type?*); start: (*type?*); stop: (*type?*)): ;
(*
 * Recursively sorts a subrange of the given array 'values´. 
 * start is the first index of the subsequence to be sorted,
 * stop is the index BEHIND the subsequence to be sorted.
 *)
VAR
  (* TODO: check and accomplish variable declarations *)
  (* Partition the array into smaller and greater elements *)
  (* Get the resulting (and final) position of the pivot element *)
  p: ???;	(* FIXME! *)
BEGIN
  (* At least 2 elements? (Less don't make sense.) *)
  IF stop >= start + 2 THEN
    (* Select a pivot element, be p its index. *)
    (* (here: randomly chosen element out of start ... stop-1) *)
    p := random(stop-start) + start;
    (* Partition the array into smaller and greater elements *)
    (* Get the resulting (and final) position of the pivot element *)
    p := partition(values, start, stop, p);
    (* Sort subsequances separately and independently ... *)

    (* ========================================================== *)
    (* ================= START PARALLEL SECTION ================= *)
    (* ========================================================== *)
    (* TODO: add the necessary code to run the threads concurrently *)
    BEGIN

      (* ----------------- START THREAD 0 ----------------- *)
      BEGIN
        (* Sort left (lower) array part *)
        quickSort(values, start, p);
      END;
      (* ------------------ END THREAD 0 ------------------ *)


      (* ----------------- START THREAD 1 ----------------- *)
      BEGIN
        (* Sort right (higher) array part *)
        quickSort(values, p+1, stop);
      END;
      (* ------------------ END THREAD 1 ------------------ *)

    END;
    (* ========================================================== *)
    (* ================== END PARALLEL SECTION ================== *)
    (* ========================================================== *)

  END;
  RETURN 0;
END quickSort;

PROCEDURE heapSort*(values: (*type?*)): ;
(*
 * Sorts the array 'values´ of numbers according to he heap sort
 * algorithm
 *)
VAR
  (* TODO: check and accomplish variable declarations *)
  (* Swap the maximum value (root of the heap) to the heap end *)
  maximum: ???;	(* FIXME! *)
  k: INTEGER;
  heapRange: INTEGER;
BEGIN
  buildMaxHeap(values);
  heapRange := length(values);
  FOR k := heapRange - 1 TO 1 BY -1 DO
    heapRange := heapRange - 1;
    (* Swap the maximum value (root of the heap) to the heap end *)
    maximum := values[0];
    values[0] := values[heapRange];
    values[heapRange] := maximum;
    maxHeapify(values, 0, heapRange);
  END;
  RETURN 0;
END heapSort;

BEGIN
  In.Open;
  Out.Open;
  REPEAT
    (* TODO: Replace "TYPE" by the the actual In procedure name for this type! *)
    In.TYPE(elementCount);
  UNTIL elementCount >= 1;
  REPEAT
    (* TODO: Replace "TYPE" by the the actual In procedure name for this type! *)
    Out.String("Filling: 1 = random, 2 = increasing, 3 = decreasing"); In.TYPE(modus);
  UNTIL modus = 1 OR modus = 2 OR modus = 3;
  FOR i := 0 TO elementCount-1 DO
    CASE modus OF
      1:
      values1[i] := random(10000);
    |
      2:
      values1[i] := i;
    |
      3:
      values1[i] := -i;
    |
    END;
  END;
  (* Copy the array for exact comparability *)
  FOR i := 0 TO elementCount-1 DO
    values2[i] := values1[i];
    values3[i] := values1[i];
  END;

  (* ========================================================== *)
  (* ================= START PARALLEL SECTION ================= *)
  (* ========================================================== *)
  (* TODO: add the necessary code to run the threads concurrently *)
  BEGIN

    (* ----------------- START THREAD 0 ----------------- *)
    BEGIN
      bubbleSort(values1);
    END;
    (* ------------------ END THREAD 0 ------------------ *)


    (* ----------------- START THREAD 1 ----------------- *)
    BEGIN
      quickSort(values2, 0, elementCount);
    END;
    (* ------------------ END THREAD 1 ------------------ *)


    (* ----------------- START THREAD 2 ----------------- *)
    BEGIN
      heapSort(values3);
    END;
    (* ------------------ END THREAD 2 ------------------ *)

  END;
  (* ========================================================== *)
  (* ================== END PARALLEL SECTION ================== *)
  (* ========================================================== *)

  ok1 := testSorted(values1);
  ok2 := testSorted(values2);
  ok3 := testSorted(values3);
  IF ~ ok1 OR ~ ok2 OR ~ ok3 THEN
    FOR i := 0 TO elementCount-1 DO
      IF values1[i] # values2[i] OR values1[i] # values3[i] THEN
        Out.String("Difference at [");
        Out.Int(i, 10);
        Out.String("]: ");
        (* TODO: Replace "TYPE" by the the actual Out procedure name for this type and add a length argument where needed! *)
        Out.TYPE(values1[i]);
        Out.String(" <-> ");
        (* TODO: Replace "TYPE" by the the actual Out procedure name for this type and add a length argument where needed! *)
        Out.TYPE(values2[i]);
        Out.String(" <-> ");
        (* TODO: Replace "TYPE" by the the actual Out procedure name for this type and add a length argument where needed! *)
        Out.TYPE(values3[i]);
        Out.Ln;
      END;
    END;
  END;
  REPEAT
    (* TODO: Replace "TYPE" by the the actual In procedure name for this type! *)
    Out.String("Show arrays (yes/no)?"); In.TYPE(show);
  UNTIL show = "yes" OR show = "no";
  IF show = "yes" THEN
    FOR i := 0 TO elementCount - 1 DO
      Out.String("[");
      Out.Int(i, 10);
      Out.String("]:\t");
      (* TODO: Replace "TYPE" by the the actual Out procedure name for this type and add a length argument where needed! *)
      Out.TYPE(values1[i]);
      Out.String("\t");
      (* TODO: Replace "TYPE" by the the actual Out procedure name for this type and add a length argument where needed! *)
      Out.TYPE(values2[i]);
      Out.String("\t");
      (* TODO: Replace "TYPE" by the the actual Out procedure name for this type and add a length argument where needed! *)
      Out.TYPE(values3[i]);
      Out.Ln;
    END;
  END;
END SORTING_TEST_MAIN.

# Collections

There are a few important concepts to know when working with the methods of the Scala collection classes:
- A _predicate_ is simply a method, function, or anonymous function that takes one or more parameters and returns a Boolean value
- anonymous function
- implied loops

## Understanding the Collections Hierarchy

At a high level, Scala’s collection classes begin with the `Traversable` and `Iterable` traits, and extend into the three main categories of sequences (Seq), sets (Set), and maps (Map). Sequences further branch off into indexed and linear sequences.
- `Traversable` trait lets you traverse an entire collection
  - it implements the behavior common to all collections in terms of a foreach method
- `Iterable` trait defines an iterator, which lets you loop through a collection’s elements one at a time
  - when using an iterator, the collection can be traversed only once, because each element is consumed during the iteration process.

`Traversable`
  - `Iterable`
    - `Seq`
    - `Set`
    - `Map`
      - `IndexedSeq`
      - `LinearSeq`

`Seq`
- `IndexedSeq`
  - `Array`
  - `StringBuilder`
  - `Range`
  - `String`
  - `Vector`
- `Buffer`
  - `ArrayBuffer`
  - `ListBuffer`
- `LinearSeq`
  - `List`
  - `Queue`
  - `LinkedList`
  - `Stack`
  - `MutableList`
  - `Stream`

`Map`
  - `HashMap`
  - `WeakHashMap`
  - `SortedMap`
  - `TreeMap`
  - `LinkedHashMap`
  - `ListMap`

`Set`
  - `BitSet`
  - `HashSet`
  - `ListSet`
  - `SortedSet`
    - `TreeSet`

## Choosing a Collection Class

## Choosing a Collection Method to Solve a Problem

## Understanding the Performance of Collections

## Declaring a Type When Creating a Collection

## Understanding Mutable Variables with Immutable Collections

## Make Vector Your “Go To” Immutable Sequence

## Make ArrayBuffer Your “Go To” Mutable Sequence

## Looping over a Collection with foreach

## Looping over a Collection with a for Loop

## Using zipWithIndex or zip to Create Loop Counters

## Using Iterators

## Transforming One Collection to Another with for/ yield

##  Transforming One Collection to Another with map

## Flattening a List of Lists with flatten

## Combining map and flatten with flatMap

## Using filter to Filter a Collection

## Extracting a Sequence of Elements from a Collection

## Splitting Sequences into Subsets (groupBy, partition, etc.)

## Walking Through a Collection with the reduce and fold Methods

## Extracting Unique Elements from a Sequence

## Merging Sequential Collections

## Merging Two Sequential Collections into Pairs with zip

## Creating a Lazy View on a Collection

## Populating a Collection with a Range

## Creating and Using Enumerations

## Tuples, for When You Just Need a Bag of Things

## Sorting a Collection

## Converting a Collection to a String with mkString

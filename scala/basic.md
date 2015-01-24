# Scalaの基本

機能の概要と簡単な使用例をすごくざっくり書きます。

手元の実行環境は2.11.4ですが、何も断りがない場合は2.10系でも動きます。2.9系でもたぶん動きます。

```bash
[aoino@aoino-local ~]$ scala
Welcome to Scala version 2.11.4 (Java HotSpot(TM) 64-Bit Server VM, Java 1.7.0_71).
Type in expressions to have them evaluated.
Type :help for more information.

scala>
```

## 変数の宣言 var/val
変数の定義/宣言方法です。Scalaには二種類あります。
valは再代入不可、varは再代入可能です。

- val
再代入不可な変数です。
初期化時に必ず値に束縛しなくてはいけません。

```scala
scala> val name: String = "John Doe"
name: String = John Doe

scala> name = "Aoino"
<console>:8: error: reassignment to val
       name = "Aoino"
            ^
```

型推論機能があるので、型の定義を省略することもできます。

```scala
scala> val name = "John Doe"
name: String = John Doe
```

- var
再代入可能な変数です。

```scala
scala> var name: String = "John Doe"
name: String = John Doe

scala> name = "Aoino"
name: String = Aoino
```

val同様型の宣言を省略できますが、宣言時に必ず初期値を定義しておく必要があります。

```scala
scala> var name: String
<console>:7: error: only classes can have declared but undefined members
(Note that variables need to be initialized to be defined)
       var name: String
           ^

scala> var name = ""
name: String = ""
```


## メソッドの定義 def
メソッドの宣言方法です。

- 仕様上もっとも明示的で厳密なメソッドの定義方法

```scala
scala> def add(x: Int, y: Int): Int = {
     |   return x + y
     | }
add: (x: Int, y: Int)Int

scala> add(1,2)
res0: Int = 3
```

- いろいろ省略できます

 - return  
メソッド内で、最後に評価された式の値が返り値となるので、明示的にreturnしない限りはreturn不要。

 - 返り値の型  
型推論機能により返り値の型が省略可能。ただし、想定と違う型に推論されることもあるので、明示的に書くのがオススメ。  
※引数の型は省略できません。

 - {}  
メソッド内の処理が式一つのみで構成されてる場合に省略可能。  

いろいろ省略した結果

```scala
scala> def add(x: Int, y: Int) = x + y
add: (x: Int, y: Int)Int

scala> add(2,3)
res0: Int = 5
```

## if-else
Scalaのifは式です。値を返します。

```scala
scala> val num = if (true) 1 else 0
num: Int = 1

scala> val str = if (false) "true" else "false"
str: String = false
```

なので条件式の評価結果がtrueとfalseで同じ型になるよう型付けされます。  
※StringとIntのように継承関係のないものはAnyになります。(Anyが共通の親と言えなくもない)

```scala
scala> val hoge = if (true) "" else 0
hoge: Any = ""

scala> class Character
defined class Character

scala> object Player extends Character
defined object Player

scala> object Enemy extends Character
defined object Enemy

scala> val character = if (true) Player else Enemy
character: Character = Player$@1ba52c2a
```

また、elseを省略することもできますが、返り値がAnyになっちゃったりUnitが返ってきたりするので、望ましくないです。

```scala
scala> val bad = if (true) ""
bad: Any = ""

scala> val bad = if (false) ""
bad: Any = ()
```

## for

Javaの拡張for文に近い振る舞いをします。

```scala
scala> for(i <- List(1, 2, 3); j <- List(1, 2, 3)) { println(i * j) }
1
2
3
2
4
6
3
6
9
```

## function(lambda)

```scala
scala> val add = (x: Int, y: Int) => x + y
add: (Int, Int) => Int = <function2>

scala> add(1,2)
res18: Int = 3
```

- 使用例とか
```scala
scala> def calc(x: Int, y: Int, f: (Int, Int) => Int) = f(x, y)
calc: (x: Int, y: Int, f: (Int, Int) => Int)Int

scala> calc(2, 3, add)
res19: Int = 5
```

ここではadd変数に束縛されている「Int型の値二つをとり、その二つを足し合わせる関数」をcalcメソッドに渡しています。
また、型が推論できる場合は以下のようにアンダースコアを用いることもできます。

```scala
scala> calc(2, 3, _ * _)
res20: Int = 6
```

解釈としては、アンダースコアがそれぞれ引数に対応します。
今回、calcメソッドは三番目の引数に「(Int, Int) => Int」をとる事が分かっているので、このような表記が可能となります。
ListやMapなど、collectionに対するmapやfilterでよく使います。

```scala
scala> List(1, 2, 3).map(_ + 1)
res21: List[Int] = List(2, 3, 4)

scala> List(1, 2, 3, 4, 5, 6).filter(_ > 3)
res24: List[Int] = List(4, 5, 6)
```

このように値として関数が扱える事をScalaに限らず「第一級関数をサポートしている」と言います。
また、calcのように関数を引数にとったり、関数を返り値として返したりする関数を「高階関数」と言います。


## pattern match
イメージはJavaでいうswitch文のすごい版です。
matchも式なので値を返します。

```scala
scala> val str = Option("Aoino")
str: Option[String] = Some(Aoino)

scala> str match {
     |   case Some(s) => s
     |   case None    => ""
     | }
res3: String = Aoino
```


## trait
イメージは実装を持てるようになったJavaのInterfaceです。
traitを別のclass/object/traitに実装することをmixin(ミックスイン)と言います。

```scala
scala> trait Human {
     |   def say = "hello"
     | }
defined trait Human

scala> trait Player {
     |   def name = "John Doe"
     | }
defined trait Player

scala> object Aoino extends Human with Player {
     |   override def name = "Aoino"
     | }
defined object Aoino

scala> Aoino.say
res1: String = hello

scala> Aoino.name
res2: String = Aoino
```

## class
定義方法はJavaと殆ど一緒です。
ただし、コンストラクターの定義方法がJavaと異なります。

```scala
scala> class User(firstName: String, lastName: String) {
     |   def fullName = firstName + lastName
     | }
defined class User

scala> new User("Naoki", "Aoyama")
res10: User = User@77bb6583

scala> res10.fullName
res11: String = NaokiAoyama
```


## case class

case classはclass定義を便利にしてくれるものです。(って認識でいいと思います)
「case」をつけて定義されたclassは、自動的に便利メソッド達を追加してくれます。

「new」が省略できたり、copyなどのメソッドが使えるようになります。

```scala
scala> case class User(name: String)
defined class User

scala> User("Aoino")
res34: User = User(Aoino)

scala> User("Aoino").copy("AAA")
res35: User = User(AAA)
```


## object
GoFのSingleton patternを言語レベルで実装したものです。

- companion object

お互いのアクセス権を共有します。つまり、お互いのprivateを指定した値または関数（メソッド）にアクセス出来ます。

```scala
class User {
  def showDefaultValue = User.defaultValue
}

object User {
  private val defaultValue = "John Doe"
}
```

※ちなみにREPLでCompanion Objectを定義したい場合は、以下のように:pasteモードを使用してください。

## tuple

tupleは異なる型の要素をまとめて扱うのに使えます。  
※型の順序も意味を持ちます。

```scala
scala> val t = (1, "hoge", 'a')
t: (Int, String, Char) = (1,hoge,a)

scala> t._1
res4: Int = 1

scala> t._2
res5: String = hoge

scala> t._3
res6: Char = a
```

## カリー化(Curry)

引数を複数とる関数/メソッドは、以下のように書くことができます。

```
scala> def repeat(str: String)(i: Int): String = str * i
repeat: (str: String)(i: Int)String

scala> repeat("hoge")(3)
res0: String = hogehogehoge
```


## implicit parameter

カリー化した関数/メソッドの最後の引数に'implicit'を付けると、スコープ内に存在する'implicit'の付く、  
評価結果と引数の型が一致する値が暗黙的に渡されます。

```
scala> implicit val defaultNum = 3
defaultNum: Int = 3

scala> def repeat(str: String)(implicit i: Int) = str * i
repeat: (str: String)(implicit i: Int)String

scala> repeat("Hoge")(2)
res2: String = HogeHoge

scala> repeat("Hoge")
res3: String = HogeHogeHoge
```


## string interpolation
2.10系で実装された機能です。  
文字列の中にScalaのコードを埋め込むことができます。

```scala
scala> val aaa = "12345"
aaa: String = 12345

scala> s"${aaa}'s length is: ${aaa.length}"
res5: String = 12345's length is: 5
```




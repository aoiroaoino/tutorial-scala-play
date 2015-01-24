# 基本的な使い方

## HTTP Routing
https://www.playframework.com/documentation/ja/2.3.x/ScalaRouting

conf/routesファイルでアクセスされるURLを定義する

```
# Home page
GET     /                           controllers.Application.index
GET     /timeline                   controllers.Application.timeline
```

左から順にHTTPメソッド、URIパターン、呼び出し定義と呼びます。  
URIはエンドポイントからの相対pathで記述し、呼び出し定義部分にアクセスされた際に呼ぶメソッドを定義します。

例えば上記の「/timeline」と定義された行はローカル環境の場合「http://localhost:9000/timeline」となります。

また、pathに含まれる値を取得してcontrollerメソッドに渡すことができます。

```
GET     /user/:id                   controllers.Application.show(id: Int)
```

## Controller
https://www.playframework.com/documentation/ja/2.3.x/ScalaActions

受け取ったrequestはAction(と呼ばれる単位で)処理をします。  
Actionの型は「play.api.mvc.Request => play.api.mvc.Result」です。

シンプルで基本的な例を示します。

```scala
def index = Action { implicit session =>
  Ok(views.html.index("Your new application is ready."))
}
```

## Template Engin

PlayではTwirlと呼ばれるテンプレートエンジンを使用しています。  
https://github.com/playframework/twirl

基本的にはHTMLで書くのですが、@マーク以降をScalaのコードとして解釈します。  
Twirlのパーサーはとっても優秀なので、Scalaのコードブロックの終了を自動的に判別します。

また、作成したテンプレートはコンパイルされ「views/Application/index.scala.html」ファイルは  
「views.html.Application.index」クラスが生成されます。

controllerの項目で例示したように使用することができます。

```html
@(tasks: List[Task])

<h1>タスク一覧</h1>

<table border=1>
<tr><th>タスク名</th><th>内容</th><th>削除</th></tr>
@for(task <- tasks) {
  <tr><td>@task.title</td><td>@task.contents</td><td>delete</td></tr>
}
</table>
```

## Form
Formのデータの流れ
https://www.playframework.com/documentation/ja/2.3.x/resources/manual/scalaGuide/main/forms/images/lifecycle.png

上記Template Enginと組み合わせてFormを利用したデータのやりとりを柔軟に(?)定義してやることができます。

- formからデータを受け取る為の器を作ります。

```scala
case class Task(title: String, contents: String)
```

- formから送られてくるデータと器となる上記case classとのmappingを定義してやります。

```scala
case class CreateForm(title: String, content: String)

...

val taskForm = Form(
  mapping(
    "title" -> text,
    "contents" -> text
  )(CreateForm.apply)(CreateForm.unapply)
)
```

- データをPOSTするためのFormを定義します。

```html
@(createForm: Form[CreateForm])

@import helper._

@helper.form(action = routes.Application.newTask) {
  @inputText(createForm("title"))
  @inputText(createForm("contents"))
  <input type="submit" value="Submit">
}
```

- controllerで、viewに定義したFormを渡します。

```
def createFormView = Action {
  Ok(views.html.create(createForm))
}
```

- Formからのデータを受け取り、結果に応じて処理を行います。

```scala
def create = Action { implicit request =>
  createForm.bindFromRequest.fold(
    error => {
      BadRequest("Error")
    },
    {
      case CreateForm(t, c) => {
        Task.create(t, c)
        Redirect(routes.Application.tasks)
      }
    }
  )
}
```


## Setting
- Playアプリの設定ファイル

Playに関する設定はconf/application.confファイルに記述します。(DB接続情報など)  
内部的にはtypesafe/configが使われています。


https://github.com/typesafehub/config
http://tototoshi.hatenablog.com/entry/20120505/1336212534


- Globalな設定

起動時/停止時に任意の処理を記述することができます。

```scala
import play.api._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Application has started")
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }

}
```


## DB連携

Playは標準でAnormと呼ばれるDBアクセスライブラリを持っています。  
が、業務で使うには些か貧弱なので他のライブラリを使うことが多いです。

- Slick
typesafe社が開発を行っているORM  
http://slick.typesafe.com/

- scalikeJDBC  
http://scalikejdbc.org/


ここではscalikeJDBCでMySQLサーバーに接続する方法をまとめます。

まず、libraryDependenciesにscalikeJDBCとMySQLへの依存を追加します。

```
libraryDependencies ++= Seq(
  ...
  "org.scalikejdbc" %% "scalikejdbc"                       % "2.2.2",
  "org.scalikejdbc" %% "scalikejdbc-config"                % "2.2.2",
  "org.scalikejdbc" %% "scalikejdbc-play-plugin"           % "2.3.4",
  "mysql"           %  "mysql-connector-java"              % "5.1.34"
)
```

conf/application.confファイルにDBの接続先とscalikeJDBCの設定を追加します。

```
...

# Database configuration
# ~~~~~
# You can declare as many datasources as you want.
# By convention, the default datasource is named `default`
#
db.default.driver=com.mysql.jdbc.Driver
db.default.url="jdbc:mysql://localhost/play2"
db.default.user=user
db.default.password=password

scalikejdbc.global.loggingSQLAndTime.enabled=true
scalikejdbc.global.loggingSQLAndTime.logLevel=debug
scalikejdbc.global.loggingSQLAndTime.warningEnabled=true
scalikejdbc.global.loggingSQLAndTime.warningThresholdMillis=1000
scalikejdbc.global.loggingSQLAndTime.warningLogLevel=warn

...
```

受け取りたいデータ構造を定義し、コンパニオンオブジェクト内にmappingの為のapplyメソッドを定義します。  
あとは下記createメソッドやfindByIdメソッドの様にデータの作成や読み出しが出来ます。

```scala
case class Task(id: Int, title: String, content: String, createdAt: DateTime)

object Task extends SQLSyntaxSupport[Task] {

  override val tableName = "tasks"

  def apply(rs: WrappedResultSet): Task = Task(
    rs.int("id"),
    rs.string("title"),
    rs.string("content"),
    rs.jodaDateTime("createdAt")
  )

  ...

  def create(title: String, content: String)(implicit dbSession: DBSession = AutoSession) = {
    sql"insert into tasks (title, content) values (${title}, ${content})".update.apply()
  }

  ...

  def findById(id: Int): Option[Task] = {
    DB readOnly { implicit session =>
      sql"select id, title, content, createdAt from tasks where id = ${id}".map(Task(_)).single.apply()
    }
  }

  ...

}
```



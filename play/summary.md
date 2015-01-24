# Playとは

公式Documentationのページより。

> Play は、現代の web アプリケーション開発に必要なコンポーネント及び API を統合した生産性の高い Java と Scala の web アプリケーションフレームワークです。
>
> Play の特徴は、ライトウェイト、ステートレス、web フレンドリーなアーキテクチャであること、機能予測のしやすさです。また、Iteratee IO をベースにしたリアクティブモデルのおかげで、スケーラブルなアプリケーションでも CPU、メモリ、スレッドなどのリソース消費が最小限になっています。


現在、1.x系と2.x系が存在する。
1.x系はJavaのWebフレームワーク。一応メンテされてる(?)が、わざわざ使う必要はない。

現在の最新安定版はv2.3.7。


# 概要

## 起動

v2.3より、playコマンドは廃止されactivatorコマンドと統合されました。
play自体はsbtのpluginとして提供されているので、基本的にはsbtプロジェクトにplay pluginを追加するだけで使えるようになります。
activatorコマンドに関しては参考資料を参照。

今回はsbtコマンドで起動させます。

```sh
sbt run
```

## ディレクトリ構成
基本的なMVCフレームワークの構成になっている。

```sh
[aoino@aoino-local play2-sample]$ tree
.
├── LICENSE
├── README
├── activator
├── activator-launch-1.2.12.jar
├── activator.bat
├── app
│   ├── controllers
│   │   └── Application.scala
│   └── views
│       ├── index.scala.html
│       └── main.scala.html
├── build.sbt
├── conf
│   ├── application.conf
│   └── routes
├── project
│   ├── build.properties
│   └── plugins.sbt
├── public
│   ├── images
│   │   └── favicon.png
│   ├── javascripts
│   │   └── hello.js
│   └── stylesheets
│       └── main.css
└── test
    ├── ApplicationSpec.scala
    └── IntegrationSpec.scala
```


# 参考資料

## ドキュメント

- Play Framework 公式  
https://www.playframework.com/


## 本

- Play Framework 2徹底入門 JavaではじめるアジャイルWeb開発  
Javaでの使い方がメインとなってるのでその辺ちょっと注意が必要。





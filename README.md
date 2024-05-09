# Group-Pets
ペット(狼と猫)をグループ管理することのできる Minecraft Plugin

# Group-Petsについて
`1.20.5`対応
spigot系列のサーバー用

[MyPet Plugin](https://github.com/MyPetORG/MyPet)を考慮しての開発

`/grouppets` コマンドに全てを集約する(狼も猫も一緒に管理)

## コマンド
permission node: `grouppets.<機能名>`
- `/grouppets create group <name[a-z,0-9]>`: グループの作成(グループ名は半角英数字のみ)
- `/grouppets delete group <name[a-z,0-9]>`: グループの削除(グループ名は半角英数字のみ)
- `/grouppets group`
  - `/grouppets group add <name[a-z,0-9]>`: 指定したグループへ追加するモード。この常態でペットを右クリックで追加する
  - `/grouppets group remove <name[a-z,0-9]>`: 指定したグループから削除するモード。この常態でペットを右クリックで削除する
  - `/grouppets group sit <name[a-z,0-9]>`: グループ内すべてのペットを座らせる
  - `/grouppets group stand <name[a-z,0-9]>`: グループ内すべてのペットを立たせる
  - `/grouppets group list`: グループのリストを表示
    <name>(petsNum), <name>(petsNum)...
  - `/grouppets group list <name[a-z,0-9]>`: グループ内のペットをすべて表示。同時に体力と名前がついていれば名前を表示、ない場合は狼か猫を表示
  - `/grouppets group sync`: 全グループのペットをチェックして、死んでいる場合はグループから除外する

## その他のイベント
スニークしながらどれかしらのグループに登録されているペットをtoggle（座らせるもしくは立たせる）した場合グループないすべてのペットを同じ状態にさせる

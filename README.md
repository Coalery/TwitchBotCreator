# TwitchBotCreator

안드로이드 환경에서 js를 통해 트위치 봇을 만드는 앱.

## 기능

매우 기본적인 기능만이 내장되어 있습니다.

- 채널 타겟 설정
- 봇 Oauth 토큰 설정
- JS를 통한 봇 개발

```javascript
function onStart() {}

function onMessageReceived(channel, badges, sender_id, sender_nickname, message) {
    return message;
}
```

## Functions

### onStart Function

`onStart` 함수는 봇이 시작될 때 호출되며, 값을 초기화할 때 사용합니다.

### onMessageReceived Function

`onMessageReceived` 함수는 메시지를 받았을 때 호출되며, 해당 함수의 리턴 값을 트위치에 보냅니다.

`channel` 매개변수는 해당 봇이 접속해 있는 채널의 아이디입니다.

`badges` 매개변수는 채팅을 보낸 사람이 가진 뱃지들의 배열입니다.

`sender_id` 매개변수는 채팅을 보낸 사람의 아이디입니다.

`sender_nickname` 매개변수는 채팅을 보낸 사람의 닉네임입니다.

`message` 매개변수는 받은 채팅의 내용입니다.

## 예정 기능

- [ ] Sqlite Database
- [ ] File Read/Write
- [ ] Manager Command

# References

[Pircbot](http://www.jibble.org/pircbot.php)

[rhino](https://github.com/mozilla/rhino)

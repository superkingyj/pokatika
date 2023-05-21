# 💫 Pokatika

![](https://velog.velcdn.com/images/superkingyj/post/c1749791-aa5f-4dba-952c-20efa7e0a9d5/image.png)

## ✨ Short Description

- POKATIKA is a mobile application that connects people at in-person events, enhancing the event experience with NFC card tapping.
- The minted NFTs contain personalized attendee information, providing an exclusive and customized event experience.

<br>

***

# 🎥 Demo Vedio
![](https://velog.velcdn.com/images/superkingyj/post/5eb29c8b-72f2-4474-a233-e34fccdcc803/image.png)

###[Press this Link]( youtube.com/watch?v=zW9jBGImv2Q)

***

# 🔨 Tech
- Framework: Spring 2.7.12
- Language: Java 15
- IDE: IntelliJ
- CI/CD: Docker
- Cloud: Aws EC2, AWS RDS, AWS S3

<br>

***

# 🎆 API
### GET /event/by-wallet/{wallet_address}
- Input: 
  ```
  User's wallet address (Path variable)
  ```
- Output example
  ```
  {
    "message": "요청을 성공적으로 처리했습니다",
    "data": {
        "eventId": 0,
        "eventTitle": "test event title",
        "address": "인천 서구 첨단동로 374",
        "startDate": "23.05.19 12:00",
        "endDate": "23.05.20 12:00",
        "walletAddress": "12345abcde",
        "twitterHandle": "@test",
        "count": 9,
        "nftImage": {image}
    }

<br>

### GET /event/by-nft/{nft_token_id}
- Input
  ```
  User's Nft token id  (Path variable)
  ```
- Output example
  ```
  {
    "message": "요청을 성공적으로 처리했습니다",
    "data": {
        "eventId": 1,
        "eventTitle": "test event title",
        "address": "인천 서구 첨단동로 374",
        "startDate": "23.05.19 12:00",
        "endDate": "23.05.20 12:00",
        "nftImage": {image}
    }

<br>

### POST /nft
- Input
  ```
  {
    "walletAddress": "12345abcde",
    "nftId": "6789fghi",
    "eventId": 1
  }
  
- Output
  ```
  {
    "message": "요청을 성공적으로 처리했습니다"
  }
  ```


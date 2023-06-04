# 💫 Pokatika

![](https://velog.velcdn.com/images/superkingyj/post/c1749791-aa5f-4dba-952c-20efa7e0a9d5/image.png)

## ✨ Short Description

- POKATIKA is a mobile application that connects people at in-person events, enhancing the event experience with NFC card tapping.
- The minted NFTs contain personalized attendee information, providing an exclusive and customized event experience.
- We use Keypom SDK so that people can mint NFT without creating wallet and saving seed pharases at the very first time.
- We use token gating function to give exclusive experience for enthusiastic users. So if event hosts want to create private events for who has particiated their events before, then they can filter attendees with minted NFT photo card collections.

<br>
<br>

# 🎥 Demo Vedio
![](https://velog.velcdn.com/images/superkingyj/post/d67ddb04-1806-4fe0-865f-ffa06fc4c093/image.png)
### [Press this Link](https://www.youtube.com/watch?v=T3L13rOU9BA&t=8s)

<br>
<br>

# 🔨 Tech
- Framework: Spring 2.7.12
- Language: Java 15
- IDE: IntelliJ
- CI/CD: Docker
- Cloud: Aws EC2, AWS RDS
- Database: MySQL, IPFS

<br>
<br>

# 🥞 ERD
![](https://velog.velcdn.com/images/superkingyj/post/0d905875-ee88-4f4e-b97f-8e89dd7f05f0/image.png)


<br>
<br>


# 🎆 API
### GET api/event/by-wallet/{wallet_address}

- description
  - Get nft_token_id and return send event data event metadata that was used during nft minting.
  - This API is used when a user registered in RSVP wants to receive NFT photo cards.

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
        "cid": {ipfs_saved_image_cid}
    }

<br>

### GET api/event/by-nft/{nft_token_id}

- description
  - Get nft_token_id and return send event data event metadata that was used during nft minting.
  - This API is used on the detailed page related to the event that the user enters when clicking on the NFT photo card.

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
        "nftImage": {ipfs_saved_image_cid}
    }

<br>

### GET api/event/by-id/{event_id}/{trial_wallet_address}

- description
  - Get event_id and user's trial_wallet_address and return send the custom nft photo card image with the user's wallet address and event metadata used to mint the nft.
  - This API is used when users want to receive NFT photo cards using a Trail account without going through the process of creating a Near wallet.
  
- Input
  ```
  Event id, User's trial wallet address (Path variable)
  ```
  
- Output
  ```
  {
    "message": "요청을 성공적으로 처리했습니다",
    "data": {
        "eventId": 1,
        "eventTitle": "이드 서울",
        "address": "서울특별시 송파구 올림픽로 300 롯데타워",
        "startDate": "23.06.02 12:00",
        "endDate": "23.06.04 12:00",
        "walletAddress": "guest01.testnet",
        "twitterHandle": null,
        "count": 0,
        "nftImageCid": "bafkreidtxhhhn7f2fab5jjykznqqvkfxn7vxiohhykzvt4bkpl77kb76ci"
    }
  }
  ```


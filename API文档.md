# API文档

注意，所有的api接口都有context path /api，也就是说，我想访问/login，实际你应该访问/api/login

[TOC]

## 上传接口

```
/upload
```

Method: POST

参数

| 名称         | 位置     | 类型      | 是否必须                           |
| ------------ | -------- | --------- | ---------------------------------- |
| token        | 请求头部 | String    | 是                                 |
| Content-Type | 请求头部 | String    | 否，默认为application/octet-stream |
| userId       | 请求参数 | Form-data | 是                                 |
| file         | 请求参数 | Form-data | 是                                 |

例：

```
/upload?userId=1&file=xxxxxxxxxxx 
```


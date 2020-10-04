import sys
import requests
from requests_toolbelt.multipart.encoder import MultipartEncoder

dirs = sys.argv[1:]
result = []
url = 'https://moral-helper.online:23456/upload'
flag = False

for i in dirs:
    part = MultipartEncoder(fields={
        'userId': '000000',
        'file': ('xxx.png', open(i, 'rb'), 'application/octet-stream')
    })
    head = {'token': 'empty token, add your token here',
            'content-type': part.content_type}
    response = requests.post(url, data=part, headers=head)
    if response.status_code != 200:
        flag = True
        break
    else:
        result.append(response.text)

if flag:
    print('Fail')
else:
    print('Upload Success:')
    for i in result:
        print(i)

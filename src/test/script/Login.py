#!/usr/bin/env python2.7

import common

def Login(phone="18810346541",passwd="888888"):
    post = common.PostData("Login",{"phone":phone,"passwd":passwd})
    response = common.HttpRequest(common.urls["login"],post)
    if common.ProcessResponse(response):
        return response["data"]["token"]
    else:
        return None


if __name__ == "__main__":
    Login("18810346541","888888")

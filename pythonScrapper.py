from flask import Flask, render_template, url_for, redirect, request, flash, session
import requests
import validators
import os
from bs4 import BeautifulSoup
import lxml
import sys
import urllib
reload(sys)
sys.setdefaultencoding('utf-8')


app = Flask(__name__)
app.config['SECRET_KEY'] = 'super secret key'
app.config['SESSION_TYPE'] = 'filesystem'


def getData(fileName,urlAppend):
    """Get web data from the website"""
    url = str(urlAppend)
    req= ""
    try:
       req = requests.get(url)
    except HTTPError, e:
         print e.code
         raise Exception("There was error in connceting to: "+url)
    except URLError, e:
         print e
         raise Exception("Invalid Url Input:"+url)
    soup  = BeautifulSoup(req.content, "lxml")
    """print(soup.prettify())"""
    wrapper = soup.find_all("div",class_="clear_bottom")
    dataDir = "finalData/data"
    imageDir = "finalData/image/"+fileName
    if not os.path.exists(dataDir):
        os.makedirs(dataDir)
    if not os.path.exists(imageDir):
        os.makedirs(imageDir)
    if os.path.exists(dataDir+"/"+fileName+".csv"):
        os.remove(dataDir+"/"+fileName+".csv")
    print("Creating File With Name :: ", fileName) 
    out = open(dataDir+"/"+fileName+".csv","a+")
    for data in wrapper:
        companyLink = data.find("div",class_="name").a["href"]
        print("The company url::",companyLink)
        result = getDetailedData(companyLink,imageDir)
        if result is not None:
           saveData(result,out)
    out.close()
    print "Data Saved, File Closed."

def getDetailedData(compLink,imgDir):
    """Entered Inside Details Page"""
    url2 = "https://www.pegaxis.com/"+compLink
    req_2 = requests.get(url2)
    soup_2 = BeautifulSoup(req_2.content,"lxml")
    outer = soup_2.find("div",class_="company_content")
    if outer is None:
        return

    imgUrl = soup_2.find("div",class_="image").img["src"]
    print("Image Url::",imgUrl)
    ist = imgUrl.rfind("/")
    lst = imgUrl.rfind("?") if "?" else 0
    if validators.url(imgUrl):
       if ist >0 and lst >0:
          sss = imgUrl[ist:lst]
          print("The value of sss:",sss)
       imgPath = imgDir+sss
    else:
       imgUrl = "https://www.pegaxis.com/images/users/picture/thumb/missing.png"
       imgPath = "missing.png"
    print("%s and %s ",imgUrl,imgPath)
    if os.path.exists(imgPath):
        os.remove(imgPath)

    urllib.urlretrieve(imgUrl,imgPath)
    toLoop = outer.find_all("div",class_="form_show")
    finalVal=[]
    for val in toLoop:
       if val is None:
          continue
       else:
          textValue=val.contents[1].find("span").text
          print("Value:: ",textValue)
          if ("," in str(textValue)):
              """repr(textValue).replace(",", "")"""
              trp = textValue.split(",")
              textValue = " ".join(trp)
          finalVal.append(textValue)
    finalVal.append(imgPath)
    return finalVal

def saveData(res,svObj):
    print("Saving Data To A CSV File")
    for i in res:
        svObj.seek(0,2)
        svObj.write(str(i))
        if res.index(i)+1 !=None:
           svObj.write(',')
    svObj.write('\n')

@app.route("/start", methods=['GET','POST'])
def start():
    if request.method=='POST':
       fileName = request.form['fileName']
       urlAppend = request.form['urlAppend']
    try:
       getData(fileName,urlAppend)
       flash("Data successfully Scraped, Give another fileName for Another Category")
    except Exception, e:
       print e
       flash("Error Occurred:: ",e)
    return redirect(url_for("index"))

@app.route("/")
def index():
    print("Server Started")
    return render_template("index.html")

if __name__ == '__main__':
    app.debug = True
    app.run()

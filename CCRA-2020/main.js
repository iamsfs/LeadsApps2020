//Mailgun initialization to send emails
var api_key = "key-af64749cd71b89c74961584bfa4f7260";
var domain = "smartswipe.support";
var mg = require("mailgun-js")({ apiKey: api_key, domain: domain });
// ---------------------------------------------------------------------------------------------------------------------

//for sending regular SMS from app
Parse.Cloud.define("sendSMSWithTwilio", function (request, response) {
  //twilio initialization to send SMS
  var logger = require("parse-server").logger;
  var twilio = require("twilio")(
    "AC439073eae4b847809b8ee31d2259c6e4",
    "899e521141a7172c4c392703108fca48"
  );
  // ---------------------------------------------------------------------------------------------------------------------

  twilio.messages
    .create({
      body: request.params.body, // Any number Twilio can deliver to
      from: "+18185842166", // A number you bought from Twilio and can use for outbound communication
      to: request.params.number, // body of the SMS message
    })
    .then(function (results) {
      console.log("Success! The SID for this SMS message is:");
      response.success("sendSMSWithTwilio sent!");
    })
    .catch(function (error) {
      logger.error("Oops! There was an error." + error);
      response.error("Uh oh, something went wrong");
    });
});
// ---------------------------------------------------------------------------------------------------------------------

//For sending email
Parse.Cloud.define("sendMailgun", function (request, response) {
  var data = {
    from: request.params.fromEmail,
    to: request.params.toEmail,
    subject: request.params.subject,
    text: request.params.message,
    html: request.params.message,
  };

  mg.messages().send(data, function (error, body) {
    console.log(body);
    console.log(error);
    if (error) {
      response.error(error.message);
    } else {
      response.success(body);
    }
  });
});
//---------------------------------------------------------------------------------------------------------------------

//For sending data to pospros
Parse.Cloud.define("saveLeadToPospros", function (request, response) {
  var Pospros = Parse.Object.extend("Pospros");
  var posporsQuery = new Parse.Query(Pospros);
  posporsQuery.equalTo("slug", request.params.app);
  posporsQuery.first({
    success: function (result) {
      var result = JSON.parse(JSON.stringify(result));
      var appName = Parse.Object.extend(request.params.app);
      var query = new Parse.Query(appName);
      query.equalTo("objectId", request.params.objectId);
      query.first({
        success: function (results) {


          var results = JSON.parse(JSON.stringify(results));
          var name = results.Name + " " + results.LastName;
          var source = results.source;
          var busninessName = results.BusinessName;
          var email = results.Email;
          var phone = results.Phone;
          var url = result.url;
          var utmSource = result.utmSource;
          var utmMedium = result.utmMedium;
          var utmCampaign = result.utmCampaign;
          var utmTerm = result.utmTerm;
          var utmContent = result.utmContent;
          var note = result.note;
          var isMobile = result.isMobile;
        
          response.success(url);
          var baseurl = "https://crm.securedmerchantapp.com/api/leads";

          return Parse.Cloud.httpRequest({
            method: 'POST',
            url: baseurl,
            headers: {
              'Content-Type': 'application/json;charset=utf-8'
            },
            body:{
              "name":name,
              "source":source,
              "dba": busninessName,
              "email": email,
              "phone":phone,
              "url":url,
              "utmSource":utmSource,
              "utmMedium":utmMedium,
              "utmCampaign":utmCampaign,
              "utmTerm":utmTerm,
              "utmContent":utmContent,
              "note":note,
              "isMobile": isMobile
            }
          }).then(function(httpResponse) {
            response.success(JSON.stringify(httpResponse));
          }, 
              function (error) {
            response.error('Request failed with response ' + error.text)
          });




        },
        error: function (error) {
          response.error(JSON.stringify(error));
        },
      });
    },
    error: function (error) {
      response.error(JSON.stringify(error));
    },
  });
});

function saveToPospors(response) {}

//---------------------------------------------------------------------------------------------------------------------

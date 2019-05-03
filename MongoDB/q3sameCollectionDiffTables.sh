db.getCollection("orders").aggregate([{$match: {Freight: { $gt: 1000 }}},{$lookup: {from: "customers",localField: "CustomerID",foreignField: "CustomerID",as: "results"}}, {$project: {_id: 0,ContactName: "$results.ContactName",Country: "$results.Country",Freight: 1}},{$sort:{Freight: -1}}]).pretty()

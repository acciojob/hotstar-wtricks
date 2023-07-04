// package com.driver.services;


// import com.driver.EntryDto.SubscriptionEntryDto;
// import com.driver.model.Subscription;
// import com.driver.model.SubscriptionType;
// import com.driver.model.User;
// import com.driver.repository.SubscriptionRepository;
// import com.driver.repository.UserRepository;
// import com.google.common.base.Optional;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import java.util.Date;
// import java.util.List;

// @Service
// public class SubscriptionService {

//     @Autowired
//     SubscriptionRepository subscriptionRepository;

//     @Autowired
//     UserRepository userRepository;

//     public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){
//         //Save The subscription Object into the Db and return the total Amount that user has to pay
//         User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();

//         Integer numberOfScreen = subscriptionEntryDto.getNoOfScreensRequired();
//         Integer amountToBePaid = 
//             subscriptionEntryDto.getSubscriptionType() == SubscriptionType.ELITE 
//                 ? numberOfScreen * 350 + 1000 :
//             subscriptionEntryDto.getSubscriptionType() == SubscriptionType.PRO 
//                 ? numberOfScreen * 250 + 800 : numberOfScreen * 200 + 500;  

//         Subscription subscription = new Subscription();
//         subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
//         subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
//         subscription.setStartSubscriptionDate(new Date());
//         subscription.setUser(user);
//         subscription.setTotalAmountPaid(amountToBePaid);
//         subscriptionRepository.save(subscription);

//         return amountToBePaid;
//     }

//     public Integer upgradeSubscription(Integer userId)throws Exception{
//         //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
//         //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
//         //update the subscription in the repository

//         User user = userRepository.findById(userId).get();
//         Subscription subscription = user.getSubscription();
//         if (subscription.getSubscriptionType() == SubscriptionType.ELITE) {
//             throw new Exception ("Already the best Subscription");
//         }


//         Integer amountToBePaid = 
//             subscription.getSubscriptionType() == SubscriptionType.PRO 
//                 ? subscription.getNoOfScreensSubscribed() * 350 + 1000 
//                 : subscription.getNoOfScreensSubscribed() * 250 + 800;  
        
//         Integer alreadyPaid = subscription.getTotalAmountPaid();
        
//         // After upgrading, amount to be paid
//         Integer amountTobePaidAfterUpgrade = amountToBePaid - alreadyPaid;
//         subscription.setStartSubscriptionDate(new Date());
//         subscription.setSubscriptionType(
//             subscription.getSubscriptionType() == SubscriptionType.PRO 
//             ? SubscriptionType.ELITE : SubscriptionType.PRO);

//         subscriptionRepository.save(subscription);

//         return amountTobePaidAfterUpgrade;
//     }

//     public Integer calculateTotalRevenueOfHotstar(){

//         //We need to find out total Revenue of hotstar : from all the subscriptions combined
//         //Hint is to use findAll function from the SubscriptionDb
//         return subscriptionRepository.getTotalRevenue();
//     }

// }

package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Subscription subscription = new Subscription();
        // int price = 0;
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        String s = subscriptionEntryDto.getSubscriptionType().toString();
        int price = 0;
        if(s.equalsIgnoreCase("basic") || s.equalsIgnoreCase("pro") || s.equalsIgnoreCase("elite")){
            if(s.equalsIgnoreCase("basic")){
                subscription.setSubscriptionType(SubscriptionType.BASIC);
                price = 500 + (200*subscriptionEntryDto.getNoOfScreensRequired());
                subscription.setTotalAmountPaid(price);
            }
            else if(s.equalsIgnoreCase("pro")){
                subscription.setSubscriptionType(SubscriptionType.PRO);
                price = 800 + (250*subscriptionEntryDto.getNoOfScreensRequired());
                subscription.setTotalAmountPaid(price);
            }
            else if(s.equalsIgnoreCase("elite")){
                subscription.setSubscriptionType(SubscriptionType.ELITE);
                price = 1000 + (350*subscriptionEntryDto.getNoOfScreensRequired());
                subscription.setTotalAmountPaid(price);
            }
        }

        subscription.setStartSubscriptionDate(new Date());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setUser(user);

        user.setSubscription(subscription);
        userRepository.save(user);

        return price;
        // return null;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId).get();
        Subscription subscription = user.getSubscription();
        int amtDif = 0;

        if(subscription.getSubscriptionType()==SubscriptionType.ELITE)
            throw new Exception("Already the best Subscription");

        else if (subscription.getSubscriptionType()==SubscriptionType.PRO) {
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            amtDif = (1000 + (350*subscription.getNoOfScreensSubscribed()))-subscription.getTotalAmountPaid();
            subscription.setTotalAmountPaid(1000 + (350*subscription.getNoOfScreensSubscribed()));
            subscription.setStartSubscriptionDate(new Date());
        }
        else if (subscription.getSubscriptionType()==SubscriptionType.BASIC) {
            subscription.setSubscriptionType(SubscriptionType.PRO);
            amtDif = (800 + (250*subscription.getNoOfScreensSubscribed()))-subscription.getTotalAmountPaid();
            subscription.setTotalAmountPaid(800 + (250*subscription.getNoOfScreensSubscribed()));
            subscription.setStartSubscriptionDate(new Date());
        }

        subscriptionRepository.save(subscription);
        return amtDif;

        //return null;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        int revenue = 0;
        for(Subscription subscription:subscriptions){
            revenue += subscription.getTotalAmountPaid();
        }
        return revenue;
        ///return null;
    }

}
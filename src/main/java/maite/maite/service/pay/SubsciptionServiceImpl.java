package maite.maite.service.pay;

import lombok.AllArgsConstructor;
import maite.maite.domain.entity.Subscription;
import maite.maite.domain.entity.User;
import maite.maite.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class SubsciptionServiceImpl implements SubscriptionService{

    private final SubscriptionRepository subscriptionRepository;

    @Override
    public void addSubscription(User user) {
        LocalDate today = LocalDate.now();
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setStartDate(today);
        subscription.setEndDate(today.plusMonths(1));
        subscription.setActive(true);

        subscriptionRepository.save(subscription);
    }

}
package br.com.nimble.gateway.payment.domain.strategy;

import br.com.nimble.gateway.payment.domain.model.Charge;

@FunctionalInterface
public interface PaymentAction {

    void execute(Charge charge);
}
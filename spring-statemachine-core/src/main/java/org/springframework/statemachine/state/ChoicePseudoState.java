/*
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.statemachine.state;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.util.Assert;

/**
 * Choice implementation of a {@link PseudoState}.
 *
 * @author Janne Valkealahti
 *
 * @param <S> the type of state
 * @param <E> the type of event
 */
public class ChoicePseudoState<S, E> implements PseudoState<S, E> {

	private final static Log log = LogFactory.getLog(ChoicePseudoState.class);
	private final List<ChoiceStateData<S, E>> choices;

	/**
	 * Instantiates a new choice pseudo state.
	 *
	 * @param choices the choices
	 */
	public ChoicePseudoState(List<ChoiceStateData<S, E>> choices) {
		this.choices = choices;
	}

	@Override
	public PseudoStateKind getKind() {
		return PseudoStateKind.CHOICE;
	}

	@Override
	public State<S, E> entry(StateContext<S, E> context) {
		State<S, E> s = null;
		for (ChoiceStateData<S, E> c : choices) {
			s = c.getState();
			if (c.guard != null && evaluateInternal(c.guard, context)) {
				break;
			}
		}
		return s;
	}

	@Override
	public void exit(StateContext<S, E> context) {
	}

	@Override
	public void addPseudoStateListener(PseudoStateListener<S, E> listener) {
	}

	@Override
	public void setPseudoStateListeners(List<PseudoStateListener<S, E>> listeners) {
	}

	private boolean evaluateInternal(Guard<S, E> guard, StateContext<S, E> context) {
		try {
			return guard.evaluate(context);
		} catch (Throwable t) {
			log.warn("Deny guard due to throw as GUARD should not error", t);
			return false;
		}
	}

	/**
	 * Data class wrapping choice {@link State} and {@link Guard}
	 * together.
	 *
	 * @param <S> the type of state
	 * @param <E> the type of event
	 */
	public static class ChoiceStateData<S, E> {
		private final StateHolder<S, E> state;
		private final Guard<S, E> guard;

		/**
		 * Instantiates a new choice state data.
		 *
		 * @param state the state holder
		 * @param guard the guard
		 */
		public ChoiceStateData(StateHolder<S, E> state, Guard<S, E> guard) {
			Assert.notNull(state, "Holder must be set");
			this.state = state;
			this.guard = guard;
		}

		/**
		 * Gets the state holder.
		 *
		 * @return the state holder
		 */
		public StateHolder<S, E> getStateHolder() {
			return state;
		}

		/**
		 * Gets the state.
		 *
		 * @return the state
		 */
		public State<S, E> getState() {
			return state.getState();
		}

		/**
		 * Gets the guard.
		 *
		 * @return the guard
		 */
		public Guard<S, E> getGuard() {
			return guard;
		}
	}
}

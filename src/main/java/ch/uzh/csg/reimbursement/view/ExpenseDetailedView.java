package ch.uzh.csg.reimbursement.view;

import java.util.Set;

import lombok.Data;

@Data
public class ExpenseDetailedView {

	private String uid;
	private UserView creator;
	private UserView financeAdmin;
	private String accounting;
	private Set<NoteView> note;
	private Set<ExpenseItemView> expenseItems;
}

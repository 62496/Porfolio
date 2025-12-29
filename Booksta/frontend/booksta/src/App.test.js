import { render, screen } from '@testing-library/react';

// Simple smoke test to verify the test setup works
describe('App', () => {
    test('test environment is configured correctly', () => {
        // Verify Jest and React Testing Library are working
        expect(true).toBe(true);
    });

    test('jest-dom matchers are available', () => {
        // Render a simple element
        render(<div data-testid="test-element">Hello</div>);

        // Verify jest-dom matchers work
        const element = screen.getByTestId('test-element');
        expect(element).toBeInTheDocument();
        expect(element).toHaveTextContent('Hello');
    });
});

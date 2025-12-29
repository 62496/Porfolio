import React, { useState, useEffect, useRef } from "react";
import Header from "../../components/layout/Header";
import Footer from "../../components/layout/Footer";
import Button from "../../components/common/Button";
import authService from "../../api/services/authService";
import FeatureCard from "../../components/cards/FeatureCard";

export default function LandingPage() {
  return (
    <div className="font-[SF Pro Display] text-[#1d1d1f] bg-white">
      <Header />
      <Hero />
      <Features />
      <CitationBlock />
      <HowItWorks />
      <Footer />
    </div>
  );
}

/* ---------------------------------------------------
    HERO
----------------------------------------------------*/
const Hero = () => {
  const [visible, setVisible] = useState(false);
  useEffect(() => setVisible(true), []);

  const isLoggedIn = authService.isAuthenticated();

  return (
    <section className="pt-[120px] pb-[100px] px-[20px] text-center bg-gradient-to-b from-[#fafaf9] to-white">
      <div className="max-w-[980px] mx-auto">
        {/* Hero Title */}
        <div
          className={`mb-[24px] fade-in ${visible ? "visible" : ""}`}
        >
          <h1 className="text-[64px] md:text-[80px] font-bold tracking-[-0.03em] text-[#1d1d1f] leading-[1.05] mb-[16px]">
            Your Reading Journey
            <br />
            <span className="bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">
              Starts Here
            </span>
          </h1>
          <p className="text-[24px] md:text-[28px] text-[#6e6e73] leading-[1.4] max-w-[700px] mx-auto">
            Track your reading, discover new books, and connect with fellow book lovers in one beautiful platform.
          </p>
        </div>

        {/* CTA Buttons */}
        <div
          className={`flex justify-center gap-[16px] fade-in delay-2 ${visible ? "visible" : ""
            }`}
        >
          <Button
            label={isLoggedIn ? "Go to library" : "Get Started Free"}
            type="primary"
            href={isLoggedIn ? "/books" : "/login"}
          />
          <Button label="Learn More" type="secondary" href="#features" />
        </div>
      </div>
    </section>
  );
};


const Features = () => (
  <section id="features" className="py-[100px] px-[20px] bg-white">
    <div className="max-w-[1200px] mx-auto">
      <div className="text-center mb-[80px]">
        <h2 className="text-[56px] font-bold tracking-[-0.03em] mb-[20px] text-[#1d1d1f]">
          Everything You Need
        </h2>
        <p className="text-[24px] text-[#6e6e73] max-w-[600px] mx-auto">
          A complete platform designed for passionate book lovers
        </p>
      </div>

      <div className="grid gap-[32px] md:grid-cols-2 lg:grid-cols-3">
        <Feature
          icon="📖"
          title="Reading Sessions"
          description="Track your reading time with detailed session logs. Monitor pages read, duration, and reading speed."
          delay={1}
        />
        <Feature
          icon="📊"
          title="Progress Tracking"
          description="Visualize your reading journey with beautiful stats. See what you've started, finished, and abandoned."
          delay={2}
        />
        <Feature
          icon="❤️"
          title="Personal Library"
          description="Build and organize your digital book collection. Mark favorites and track ownership."
          delay={3}
        />
        <Feature
          icon="👥"
          title="Follow Authors"
          description="Stay updated with your favorite authors and book series. Never miss a new release."
          delay={4}
        />
        <Feature
          icon="💬"
          title="Connect & Share"
          description="Send messages to fellow readers. Share thoughts, recommendations, and reading experiences."
          delay={5}
        />
        <Feature
          icon="🎯"
          title="Reading Habits"
          description="Understand your reading patterns with detailed analytics and insights into your behavior."
          delay={6}
        />
      </div>
    </div>
  </section>
);

const Feature = ({ title, description, delay }) => {
  const ref = useRef(null);
  const [v, setV] = useState(false);

  useEffect(() => {
    const ob = new IntersectionObserver(
      ([e]) => e.isIntersecting && setV(true),
      { threshold: 0.2 }
    );
    const currentRef = ref.current;
    if (currentRef) ob.observe(currentRef);
    return () => {
      if (currentRef) ob.unobserve(currentRef);
    };
  }, []);

  return (
    <div
      ref={ref}
      className={`group bg-gradient-to-br from-white to-[#fafaf9] border-2 border-[#e5e5e7] rounded-[24px] p-[48px] text-left hover:shadow-2xl hover:border-blue-500 hover:from-blue-50 hover:to-purple-50 transition-all duration-500 stagger delay-${delay} ${v ? "visible" : ""
        }`}
    >
      <div className="mb-[32px]">
        <div className="w-[60px] h-[4px] bg-gradient-to-r from-blue-500 to-purple-600 rounded-full group-hover:w-[80px] transition-all duration-300"></div>
      </div>

      <h3 className="text-[28px] font-bold mb-[16px] tracking-[-0.02em] text-[#1d1d1f] group-hover:text-blue-600 transition-colors duration-300">
        {title}
      </h3>
      <p className="text-[17px] text-[#6e6e73] leading-[1.7]">
        {description}
      </p>
    </div>
  );
};



const CitationBlock = () => {
  const ref = useRef(null);
  const [v, setV] = useState(false);

  useEffect(() => {
    const ob = new IntersectionObserver(
      ([e]) => e.isIntersecting && setV(true),
      { threshold: 0.3 }
    );
    const currentRef = ref.current;
    if (currentRef) ob.observe(currentRef);
    return () => {
      if (currentRef) ob.unobserve(currentRef);
    };
  }, []);

  return (
    <section
      ref={ref}
      className="py-[120px] px-[20px] text-center bg-gradient-to-b from-[#fafaf9] via-white to-[#fafaf9]"
    >
      <div
        className={`max-w-[900px] mx-auto fade-in ${v ? "visible" : ""
          }`}
      >
        <div className="text-[40px] md:text-[48px] font-bold text-[#1d1d1f] mb-[24px] leading-[1.3] italic">
          "A reader lives a thousand lives before he dies... The man who never reads lives only one."
        </div>
        <div className="text-[24px] text-[#6e6e73] font-medium">
          — George R.R. Martin
        </div>
      </div>
    </section>
  );
};


const HowItWorks = () => (
  <section id="how-it-works" className="py-[100px] px-[20px] bg-white">
    <div className="max-w-[1200px] mx-auto">
      <div className="text-center mb-[80px]">
        <h2 className="text-[56px] font-bold tracking-[-0.03em] mb-[20px] text-[#1d1d1f]">
          Get Started in Minutes
        </h2>
        <p className="text-[24px] text-[#6e6e73] max-w-[600px] mx-auto">
          Join thousands of readers tracking their literary journey
        </p>
      </div>

      <div className="grid gap-[48px] md:grid-cols-3">
        <Step
          number="1"
          title="Create Your Account"
          description="Sign up in seconds with your email. Set up your reading profile and preferences to get started."
          delay={1}
        />
        <Step
          number="2"
          title="Build Your Library"
          description="Browse our extensive catalog and add books to your personal collection. Mark what you own and love."
          delay={2}
        />
        <Step
          number="3"
          title="Start Tracking"
          description="Begin reading sessions, track your progress, and discover insights about your reading habits."
          delay={3}
        />
      </div>

      {/* Final CTA */}
      <div className="text-center mt-[80px]">
        <Button
          label="Start Your Reading Journey"
          type="primary"
          href="/my-books"
        />
        <p className="text-[15px] text-[#86868b] mt-[16px]">
          Free forever. No credit card required.
        </p>
      </div>
    </div>
  </section>
);

const Step = ({ number, title, description, delay }) => {
  const ref = useRef(null);
  const [v, setV] = useState(false);

  useEffect(() => {
    const ob = new IntersectionObserver(
      ([e]) => e.isIntersecting && setV(true),
      { threshold: 0.2 }
    );
    const currentRef = ref.current;
    if (currentRef) ob.observe(currentRef);
    return () => {
      if (currentRef) ob.unobserve(currentRef);
    };
  }, []);

  return (
    <div
      ref={ref}
      className={`group relative bg-gradient-to-br from-white to-[#fafaf9] border-2 border-[#e5e5e7] rounded-[24px] p-[48px] text-left hover:shadow-2xl hover:border-blue-500 hover:from-blue-50 hover:to-purple-50 transition-all duration-500 scale-in delay-${delay} ${v ? "visible" : ""
        }`}
    >
      {/* Step indicator line */}
      <div className="mb-[32px]">
        <div className="flex items-center gap-3">
          <div className="text-[14px] font-bold text-[#6e6e73] group-hover:text-blue-600 transition-colors duration-300">
            STEP {number}
          </div>
          <div className="flex-1 h-[3px] bg-gradient-to-r from-blue-500 to-purple-600 rounded-full opacity-60 group-hover:opacity-100 transition-all duration-300"></div>
        </div>
      </div>

      <h3 className="text-[28px] font-bold mb-[16px] tracking-[-0.02em] text-[#1d1d1f] group-hover:text-blue-600 transition-colors duration-300">
        {title}
      </h3>
      <p className="text-[17px] text-[#6e6e73] leading-[1.7]">
        {description}
      </p>
    </div>
  );
};

